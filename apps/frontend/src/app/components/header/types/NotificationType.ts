export interface NotificationProps {
  id: number;
  type: 'warning' | 'info' | 'success';
  message: string;
  time: string;
}
