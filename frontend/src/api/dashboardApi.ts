import axiosClient from './axiosClient';
import { ApiResponse, DashboardSummary, ResidentDashboardSummary } from '../types';

export const dashboardApi = {
  getAdminSummary: async (): Promise<ApiResponse<DashboardSummary>> => {
    const response = await axiosClient.get<ApiResponse<DashboardSummary>>('/dashboard/summary');
    return response.data;
  },

  getResidentSummary: async (): Promise<ApiResponse<ResidentDashboardSummary>> => {
    const response = await axiosClient.get<ApiResponse<ResidentDashboardSummary>>('/dashboard/resident');
    return response.data;
  },
};
