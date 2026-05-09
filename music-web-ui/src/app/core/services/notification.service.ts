// src/app/core/services/notification.service.ts

import { Injectable, Injector } from '@angular/core';
import {
  BehaviorSubject,
  forkJoin,
  map,
  Observable,
  switchMap,
  tap,
} from 'rxjs';
import { CompatClient, Stomp, IFrame, Frame } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {
  Notification,
  NotificationResponse,
} from '../models/notification.model';
import { ProfileService } from './profile.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ApiResponse } from '../models/api_response';
import { environment } from '../../../environments/environment';
@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private baseUrl = environment.notificationApi;
  private stompClient: CompatClient | null = null;
  private connected = false;
  private messageSubject = new BehaviorSubject<Notification | null>(null);
  private notificationsSubject = new BehaviorSubject<Notification[]>([]);
  private unreadCountSubject = new BehaviorSubject<number>(0);

  constructor(
    private profileService: ProfileService,
    private http: HttpClient
  ) {}

  public messages$ = this.messageSubject.asObservable();
  public notifications$ = this.notificationsSubject.asObservable();
  public unreadCount$ = this.unreadCountSubject.asObservable();

  private updateNotifications(notifications: Notification[]) {
    this.notificationsSubject.next(notifications);
    this.updateUnreadCount(notifications);
  }

  private updateUnreadCount(notifications: Notification[]) {
    const unreadCount = notifications.filter((n) => !n.read).length;
    this.unreadCountSubject.next(unreadCount);
  }

  public addNotification(notification: Notification) {
    const currentNotifications = this.notificationsSubject.value;
    // Kiểm tra xem notification đã tồn tại chưa
    const exists = currentNotifications.some(
      (n) =>
        n.id === notification.id ||
        (n.type === notification.type &&
          n.sender.userId === notification.sender.userId &&
          n.createdAt === notification.createdAt)
    );

    if (!exists) {
      const updatedNotifications = [notification, ...currentNotifications];
      this.updateNotifications(updatedNotifications);
      this.messageSubject.next(notification);
    }
  }

  public markNotificationsAsRead(ids: string[]): Observable<any> {
    return this.http
      .put(`${this.baseUrl}/notification-service/notifications/bulk`, null, {
        params: new HttpParams().set('ids', ids.join(',')),
      })
      .pipe(
        tap(() => {
          const currentNotifications = this.notificationsSubject.value;
          const updatedNotifications = currentNotifications.map(
            (notification) => {
              if (ids.includes(notification.id)) {
                return { ...notification, read: true };
              }
              return notification;
            }
          );
          this.updateNotifications(updatedNotifications);
        })
      );
  }

  // /** Kết nối tới endpoint Spring STOMP/SockJS */
  public connect(): void {
    if (this.connected) {
      return;
    }
    const token = localStorage.getItem('auth_token');
    const socket = new SockJS(
      `${this.baseUrl}/notification-service/ws-notification?token=${token}`
    );
    this.stompClient = Stomp.over(socket);
    // Tùy chọn: tắt log STOMP nếu cần
    // this.stompClient.debug = () => {};

    this.stompClient.connect(
      {},
      (frame: IFrame) => {
        console.log('✅ STOMP Connected:', frame);
        this.connected = true;
        // Subscribe tới destination user-specific
        this.stompClient?.subscribe('/user/queue/message', (message) => {
          const body = JSON.parse(message.body);
          const sender = this.profileService.getProfileById(body.senderId);
          const recipient = this.profileService.getProfileById(
            body.recipientId
          );

          forkJoin([sender, recipient]).subscribe(([sender, recipient]) => {
            const noti: Notification = {
              id: body.id,
              message: body.message,
              type: body.type,
              createdAt: body.createdAt,
              read: body.read,
              sender: sender.data,
              recipient: recipient.data,
              trackId: body.trackId,
              commentId: body.commentId,
            };

            // Chỉ gọi addNotification, không cần gọi messageSubject.next
            this.addNotification(noti);
          });
        });
      },
      (error: Frame | string) => {
        console.error('❌ STOMP error:', error);
        this.connected = false;
      }
    );
  }
  /**
   * Gửi message tới server,
   * server sẽ route qua @MessageMapping("/send/message")
   */
  public sendMessage(recipient: string, content: string): void {
    if (this.connected && this.stompClient?.connected) {
      const payload = { recipient, content };
      this.stompClient.send('/app/send/message', {}, JSON.stringify(payload));
    } else {
      console.warn('⚠️ Cannot send, STOMP not connected');
    }
  }
  /** Ngắt kết nối WebSocket */
  public disconnect(): void {
    if (this.stompClient) {
      this.stompClient.disconnect(() => {
        console.log('🔌 STOMP Disconnected');
        this.connected = false;
      });
    }
  }

  public getNotificationIds(): Observable<ApiResponse<string[]>> {
    return this.http.get<ApiResponse<string[]>>(
      `${this.baseUrl}/notification-service/notifications/all`
    );
  }

  public getNotificationByIds(ids: string[]): Observable<Notification[]> {
    return this.http
      .get<ApiResponse<NotificationResponse[]>>(
        `${this.baseUrl}/notification-service/notifications/bulk?ids=${ids}`
      )
      .pipe(
        switchMap((responses) =>
          this.mapNotificationResponsesToNotifications(responses.data)
        ),
        tap((notifications) => {
          this.updateNotifications(notifications);
        })
      );
  }
  private mapNotificationResponseToNotification(
    response: NotificationResponse
  ): Observable<Notification> {
    const sender = this.profileService.getProfileById(response.senderId);
    const recipient = this.profileService.getProfileById(response.recipientId);
    return forkJoin([sender, recipient]).pipe(
      map(([sender, recipient]) => {
        return {
          id: response.id,
          message: response.message,
          type: response.type,
          createdAt: response.createdAt.toString(),
          read: response.read,
          trackId: response.trackId,
          commentId: response.commentId,
          sender: sender.data,
          recipient: recipient.data,
        };
      })
    );
  }
  private mapNotificationResponsesToNotifications(
    responses: NotificationResponse[]
  ): Observable<Notification[]> {
    console.log(responses);
    const senderIds = [
      ...new Set(responses.map((response) => response.senderId)),
    ];
    const recipientIds = [
      ...new Set(responses.map((response) => response.recipientId)),
    ];
    const senderObservable = this.profileService.getProfileByIds(senderIds);
    const recipientObservable =
      this.profileService.getProfileByIds(recipientIds);
    return forkJoin([senderObservable, recipientObservable]).pipe(
      map(([senders, recipients]) => {
        return responses
          .map((response) => {
            const sender = senders.data.find(
              (sender) => sender.userId === response.senderId
            );

            const recipient = recipients.data.find(
              (recipient) => recipient.userId === response.recipientId
            );

            if (!sender || !recipient) return null;
            const notification: Notification = {
              id: response.id,
              message: response.message,
              type: response.type,
              createdAt: response.createdAt.toString(),
              read: response.read,
              sender: sender,
              recipient: recipient,
              ...(response.trackId && { trackId: response.trackId }),
              ...(response.commentId && { commentId: response.commentId }),
            };

            return notification;
          })
          .filter((n): n is Notification => n !== null);
      })
    );
  }
}
