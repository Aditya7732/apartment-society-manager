import axiosClient from './axiosClient';
import { LoginResponse, ApiResponse } from '../types';

export const authApi = {
  login: async (credentials: { username: string; password: string }): Promise<ApiResponse<LoginResponse>> => {
    const response = await axiosClient.post<any>('/auth/login', {
      usernameOrEmail: credentials.username,
      password: credentials.password,
    });

    const resData = response.data;
    const user = resData.user || {
      id: resData.userId,
      username: resData.username,
      email: resData.email,
      fullName: resData.fullName,
      roles: resData.roles || [],
      residentId: resData.residentId,
      flatId: resData.flatId,
      flatNumber: resData.flatNumber,
      active: true,
    };

    return {
      success: true,
      data: {
        accessToken: resData.accessToken,
        refreshToken: resData.refreshToken,
        tokenType: resData.tokenType || 'Bearer',
        user: user,
      },
    };
  },

  changePassword: async (currentPassword: string, newPassword: string): Promise<ApiResponse<void>> => {
    const response = await axiosClient.post<ApiResponse<void>>('/auth/change-password', { currentPassword, newPassword });
    return response.data;
  },
};

