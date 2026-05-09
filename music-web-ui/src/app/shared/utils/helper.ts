import { Notification } from '../../core/models/notification.model';

function createPlaceholders(array: any[]): any[] {
  return Array(Math.max(6 - array.length, 0));
}
function getLast7Days(): { fromDate: string; toDate: string } {
  const today = new Date(); // Ngày hiện tại
  const sevenDaysAgo = new Date();
  sevenDaysAgo.setDate(today.getDate() - 6); // 6 ngày trước để tính cả hôm nay là 7 ngày

  const fromDate = formatDate(sevenDaysAgo);
  const toDate = formatDate(today);

  return { fromDate, toDate };
}

function formatDate(date: Date): string {
  const day = String(date.getDate()).padStart(2, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0'); // Month: 0-11
  const year = date.getFullYear();
  return `${day}/${month}/${year}`; // Format dd/MM/yyyy
}

function getNotificationType(notification: Notification): string {
  switch (notification.type) {
    case 'Like':
      return 'Liked your track';
    case 'Follow':
      return 'Followed you';
    case 'Comment':
      return `${notification.message} on your track`;
    case 'Mention':
      return 'Mentioned you';
    default:
      return '';
  }
}

function navigateToNotification(notification: Notification) {
  switch (notification.type) {
    case 'Like':
      return '/song/' + notification.trackId;
    case 'Follow':
      return '/profile/' + notification.sender.userId;
    case 'Comment':
      return '/song/' + notification.trackId;
    case 'Mention':
      return '/users/' + notification.sender.userId;
    default:
      return '';
  }
}
export {
  createPlaceholders,
  getLast7Days,
  formatDate,
  getNotificationType,
  navigateToNotification,
};
