import { axiosClient } from './axiosClient';
import { Notice, ApiResponse, PageResponse } from '../types';

export interface CreateNoticeRequest {
  title: string;
  content: string;
  priority: string;
  targetAudience: string;
  expiresAt?: string;
}

export const noticeApi = {
  createNotice: async (data: CreateNoticeRequest): Promise<ApiResponse<Notice>> => {
    const response = await axiosClient.post<ApiResponse<Notice>>('/notices', data);
    return response.data;
  },

  getAllNotices: async (params?: { page?: number; size?: number }): Promise<ApiResponse<PageResponse<Notice>>> => {
    const response = await axiosClient.get<ApiResponse<PageResponse<Notice>>>('/notices', { params });
    return response.data;
  },

  getActiveNotices: async (): Promise<ApiResponse<Notice[]>> => {
    const response = await axiosClient.get<ApiResponse<Notice[]>>('/notices/active');
    return response.data;
  },

  getNoticeById: async (id: string | number): Promise<ApiResponse<Notice>> => {
    const response = await axiosClient.get<ApiResponse<Notice>>(`/notices/${id}`);
    return response.data;
  },

  deleteNotice: async (id: string | number): Promise<ApiResponse<void>> => {
    const response = await axiosClient.delete<ApiResponse<void>>(`/notices/${id}`);
    return response.data;
  },
};
