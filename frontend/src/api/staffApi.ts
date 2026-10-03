import { axiosClient } from './axiosClient';
import { Staff, ApiResponse, PageResponse } from '../types';

export interface CreateStaffRequest {
  fullName: string;
  phone: string;
  role: string;
  joiningDate?: string;
  salary?: number;
  emergencyContact?: string;
  agencyName?: string;
  shiftTiming?: string;
}

export const staffApi = {
  createStaff: async (data: CreateStaffRequest): Promise<ApiResponse<Staff>> => {
    const response = await axiosClient.post<ApiResponse<Staff>>('/staff', data);
    return response.data;
  },

  getAllStaff: async (params?: { page?: number; size?: number; activeOnly?: boolean }): Promise<ApiResponse<PageResponse<Staff>>> => {
    const response = await axiosClient.get<ApiResponse<PageResponse<Staff>>>('/staff', { params });
    return response.data;
  },

  getStaffById: async (id: string | number): Promise<ApiResponse<Staff>> => {
    const response = await axiosClient.get<ApiResponse<Staff>>(`/staff/${id}`);
    return response.data;
  },

  updateStaff: async (id: string | number, data: Partial<CreateStaffRequest> & { isCurrentEmployee?: boolean }): Promise<ApiResponse<Staff>> => {
    const response = await axiosClient.put<ApiResponse<Staff>>(`/staff/${id}`, data);
    return response.data;
  },
};
