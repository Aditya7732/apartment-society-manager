import { axiosClient } from './axiosClient';
import { Complaint, ComplaintComment, ApiResponse, PageResponse } from '../types';

export interface CreateComplaintRequest {
  title: string;
  description: string;
  category: string;
  priority: string;
}

export interface UpdateComplaintStatusRequest {
  status: string;
  assignedStaffId?: string | number;
  resolutionNotes?: string;
}

export const complaintApi = {
  createComplaint: async (data: CreateComplaintRequest): Promise<ApiResponse<Complaint>> => {
    const response = await axiosClient.post<ApiResponse<Complaint>>('/complaints', data);
    return response.data;
  },

  getAllComplaints: async (params?: { page?: number; size?: number; status?: string; category?: string; priority?: string }): Promise<ApiResponse<PageResponse<Complaint>>> => {
    const response = await axiosClient.get<ApiResponse<PageResponse<Complaint>>>('/complaints', { params });
    return response.data;
  },

  getMyComplaints: async (params?: { page?: number; size?: number }): Promise<ApiResponse<PageResponse<Complaint>>> => {
    const response = await axiosClient.get<ApiResponse<PageResponse<Complaint>>>('/complaints/my', { params });
    return response.data;
  },

  getComplaintById: async (id: string | number): Promise<ApiResponse<Complaint>> => {
    const response = await axiosClient.get<ApiResponse<Complaint>>(`/complaints/${id}`);
    return response.data;
  },

  updateStatus: async (id: string | number, data: UpdateComplaintStatusRequest): Promise<ApiResponse<Complaint>> => {
    const response = await axiosClient.put<ApiResponse<Complaint>>(`/complaints/${id}/status`, data);
    return response.data;
  },

  addComment: async (id: string | number, comment: string): Promise<ApiResponse<ComplaintComment>> => {
    const response = await axiosClient.post<ApiResponse<ComplaintComment>>(`/complaints/${id}/comments`, { comment });
    return response.data;
  },
};
