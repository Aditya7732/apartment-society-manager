import axiosClient from './axiosClient';
import { AuditLog, ApiResponse, PageResponse } from '../types';

export const auditLogApi = {
  getLogs: async (params?: { page?: number; size?: number; entityName?: string; action?: string }): Promise<ApiResponse<PageResponse<AuditLog>>> => {
    const response = await axiosClient.get<ApiResponse<PageResponse<AuditLog>>>('/audit-logs', { params });
    return response.data;
  },
};
