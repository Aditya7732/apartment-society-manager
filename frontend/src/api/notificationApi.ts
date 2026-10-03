import { axiosClient } from './axiosClient';
import { AppNotification, ApiResponse } from '../types';

export const notificationApi = {
  getMyNotifications: async (): Promise<ApiResponse<AppNotification[]>> => {
    const response = await axiosClient.get<ApiResponse<AppNotification[]>>('/notifications');
    return response.data;
  },

  getUnreadCount: async (): Promise<ApiResponse<number>> => {
    const response = await axiosClient.get<ApiResponse<number>>('/notifications/unread-count');
    return response.data;
  },

  markAsRead: async (id: string | number): Promise<ApiResponse<void>> => {
    const response = await axiosClient.patch<ApiResponse<void>>(`/notifications/${id}/read`);
    return response.data;
  },

  markAllAsRead: async (): Promise<ApiResponse<void>> => {
    const response = await axiosClient.patch<ApiResponse<void>>('/notifications/read-all');
    return response.data;
  },
};
