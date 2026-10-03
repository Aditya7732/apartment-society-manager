import { axiosClient } from './axiosClient';
import { UserInfo, ApiResponse, PageResponse } from '../types';

export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
  phone: string;
  roles: string[];
}

export const userApi = {
  getAllUsers: async (params?: { page?: number; size?: number }): Promise<ApiResponse<PageResponse<UserInfo>>> => {
    const response = await axiosClient.get<ApiResponse<PageResponse<UserInfo>>>('/users', { params });
    return response.data;
  },

  createUser: async (data: CreateUserRequest): Promise<ApiResponse<UserInfo>> => {
    const response = await axiosClient.post<ApiResponse<UserInfo>>('/users', data);
    return response.data;
  },

  toggleUserStatus: async (id: string | number, active = false): Promise<ApiResponse<UserInfo>> => {
    const response = await axiosClient.patch<ApiResponse<UserInfo>>(`/users/${id}/status`, null, {
      params: { active },
    });
    return response.data;
  },
};
