import { axiosClient } from './axiosClient';
import { MaintenanceBill, PageResponse, BillStatus } from '../types';

export const billApi = {
  search: async (params?: {
    flatId?: string;
    billingPeriod?: string;
    status?: BillStatus;
    search?: string;
    page?: number;
    size?: number;
  }): Promise<PageResponse<MaintenanceBill>> => {
    const res = await axiosClient.get<PageResponse<MaintenanceBill>>('/maintenance-bills', { params });
    return res.data;
  },

  getAllBills: async (params?: {
    flatId?: string;
    billingPeriod?: string;
    status?: BillStatus;
    search?: string;
    page?: number;
    size?: number;
  }): Promise<PageResponse<MaintenanceBill>> => {
    return billApi.search(params);
  },

  getById: async (id: string): Promise<MaintenanceBill> => {
    const res = await axiosClient.get<MaintenanceBill>(`/maintenance-bills/${id}`);
    return res.data;
  },

  getByFlat: async (flatId: string): Promise<MaintenanceBill[]> => {
    const res = await axiosClient.get<MaintenanceBill[]>(`/maintenance-bills/flat/${flatId}`);
    return res.data;
  },

  create: async (data: any): Promise<MaintenanceBill> => {
    const todayStr = new Date().toISOString().split('T')[0];
    const dueStr = data.dueDate || new Date(Date.now() + 15 * 86400000).toISOString().split('T')[0];
    const payload = {
      flatId: data.flatId,
      billingPeriod: data.billingPeriod || `${new Date().getFullYear()}-${String(new Date().getMonth() + 1).padStart(2, '0')}`,
      billDate: data.billDate || todayStr,
      dueDate: dueStr,
      baseAmount: data.baseAmount || 3500,
      parkingCharges: data.parkingCharges || 0,
      waterCharges: data.waterCharges || 0,
      lateFee: data.lateFee || 0,
      otherCharges: data.otherCharges || 0,
      discount: data.discount || 0,
      taxAmount: data.taxAmount || 0,
      notes: data.notes || '',
    };
    const res = await axiosClient.post<MaintenanceBill>('/maintenance-bills', payload);
    return res.data;
  },

  batchGenerate: async (data: any): Promise<MaintenanceBill[]> => {
    const period = data.billingPeriod || `${data.billingYear || new Date().getFullYear()}-${String(data.billingMonth || new Date().getMonth() + 1).padStart(2, '0')}`;
    const todayStr = new Date().toISOString().split('T')[0];
    const dueStr = data.dueDate || new Date(Date.now() + 15 * 86400000).toISOString().split('T')[0];
    const payload = {
      buildingId: data.buildingId || null,
      billingPeriod: period,
      billDate: data.billDate || todayStr,
      dueDate: dueStr,
      baseAmount: data.baseAmount ?? (data.ratePerSqFt ? data.ratePerSqFt * 1000 : 3500),
      defaultWaterCharges: data.defaultWaterCharges ?? data.fixedWaterCharges ?? 300,
      defaultParkingCharges: data.defaultParkingCharges ?? data.fixedParkingCharges ?? 500,
      notes: data.notes || `Batch bill generated for period ${period}`,
    };
    const res = await axiosClient.post<MaintenanceBill[]>('/maintenance-bills/batch', payload);
    return res.data;
  },

  generateBatchBills: async (data: any): Promise<MaintenanceBill[]> => {
    return billApi.batchGenerate(data);
  },

  cancel: async (id: string): Promise<MaintenanceBill> => {
    const res = await axiosClient.patch<MaintenanceBill>(`/maintenance-bills/${id}/cancel`);
    return res.data;
  },

  downloadPdf: async (id: string): Promise<Blob> => {
    const res = await axiosClient.get(`/maintenance-bills/${id}/pdf`, {
      responseType: 'blob',
    });
    return res.data;
  },
};
