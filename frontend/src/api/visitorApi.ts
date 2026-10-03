import { axiosClient } from './axiosClient';
import { Visitor, ApiResponse, PageResponse } from '../types';

export interface PreRegisterVisitorRequest {
  flatId: string | number;
  visitorName: string;
  phone?: string;
  phoneNumber?: string;
  purpose: string;
  expectedArrival?: string;
  vehicleNumber?: string;
}

export interface GateCheckInRequest {
  flatId: string | number;
  visitorName: string;
  phone?: string;
  phoneNumber?: string;
  purpose: string;
  vehicleNumber?: string;
  badgeNumber?: string;
}

export const visitorApi = {
  preRegister: async (data: PreRegisterVisitorRequest): Promise<ApiResponse<Visitor>> => {
    const payload = {
      flatId: data.flatId,
      visitorName: data.visitorName,
      phoneNumber: data.phoneNumber || data.phone,
      purpose: data.purpose,
      expectedArrival: data.expectedArrival || new Date().toISOString().slice(0, 19),
      vehicleNumber: data.vehicleNumber,
    };
    const response = await axiosClient.post<ApiResponse<Visitor>>('/visitors/pre-register', payload);
    return response.data;
  },

  gateCheckIn: async (data: GateCheckInRequest): Promise<ApiResponse<Visitor>> => {
    const payload = {
      flatId: data.flatId,
      visitorName: data.visitorName,
      phoneNumber: data.phoneNumber || data.phone,
      purpose: data.purpose,
      expectedArrival: new Date().toISOString().slice(0, 19),
      vehicleNumber: data.vehicleNumber,
    };
    const res: any = await axiosClient.post('/visitors/pre-register', payload);
    const created = res.data?.data || res.data;
    if (created && created.id) {
      const checkInRes = await axiosClient.patch(`/visitors/${created.id}/check-in`);
      return checkInRes.data;
    }
    return res.data;
  },

  checkIn: async (id: string | number): Promise<ApiResponse<Visitor>> => {
    const response = await axiosClient.patch<ApiResponse<Visitor>>(`/visitors/${id}/check-in`);
    return response.data;
  },

  checkOut: async (id: string | number): Promise<ApiResponse<Visitor>> => {
    const response = await axiosClient.patch<ApiResponse<Visitor>>(`/visitors/${id}/check-out`);
    return response.data;
  },

  cancel: async (id: string | number): Promise<ApiResponse<Visitor>> => {
    const response = await axiosClient.patch<ApiResponse<Visitor>>(`/visitors/${id}/cancel`);
    return response.data;
  },

  getAllVisitors: async (params?: { page?: number; size?: number; status?: string }): Promise<ApiResponse<PageResponse<Visitor>>> => {
    const response = await axiosClient.get<ApiResponse<PageResponse<Visitor>>>('/visitors', { params });
    return response.data;
  },

  getActiveVisitors: async (): Promise<any> => {
    const response = await axiosClient.get<any>('/visitors', { params: { status: 'CHECKED_IN', size: 100 } });
    const data = response.data;
    if (Array.isArray(data)) return data;
    if (data?.content && Array.isArray(data.content)) return data.content;
    if (data?.data?.content && Array.isArray(data.data.content)) return data.data.content;
    if (Array.isArray(data?.data)) return data.data;
    return [];
  },

  getMyVisitors: async (params?: { page?: number; size?: number }): Promise<ApiResponse<PageResponse<Visitor>>> => {
    const response = await axiosClient.get<ApiResponse<PageResponse<Visitor>>>('/visitors', { params });
    return response.data;
  },
};
