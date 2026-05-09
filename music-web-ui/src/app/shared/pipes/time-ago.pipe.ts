import { Pipe, PipeTransform } from '@angular/core';
import { formatDistanceToNow } from 'date-fns';
@Pipe({
  name: 'timeAgo',
  standalone: false,
})
export class TimeAgoPipe implements PipeTransform {
  transform(value: string | Date): string {
    const parsedDate = new Date(
      typeof value === 'string' ? value.split('.')[0] : value
    );
    return formatDistanceToNow(parsedDate, { addSuffix: true });
  }
}
