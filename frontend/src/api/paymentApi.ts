import { axiosClient } from './axiosClient';
import { Payment, ApiResponse, PageResponse } from '../types';

export interface RecordPaymentRequest {
  billId: string | number;
  amount?: number;
  amountPaid?: number;
  paymentMethod: string;
  transactionId?: string;
  transactionReference?: string;
  notes?: string;
  remarks?: string;
}

export interface VerifyPaymentRequest {
  status: 'SUCCESS' | 'FAILED';
  remarks?: string;
}

export const paymentApi = {
  recordPayment: async (data: RecordPaymentRequest): Promise<ApiResponse<Payment>> => {
    const payload = {
      billId: data.billId,
      amount: data.amount ?? data.amountPaid,
      paymentMethod: data.paymentMethod || 'UPI',
      transactionId: data.transactionId || data.transactionReference || `TXN-${Date.now()}`,
      notes: data.notes || data.remarks || '',
    };
    const response = await axiosClient.post<ApiResponse<Payment>>('/payments', payload);
    return response.data;
  },

  getAllPayments: async (params?: { page?: number; size?: number; status?: string }): Promise<ApiResponse<PageResponse<Payment>>> => {
    const response = await axiosClient.get<ApiResponse<PageResponse<Payment>>>('/payments', { params });
    return response.data;
  },

  getPaymentsByBill: async (billId: string | number): Promise<ApiResponse<Payment[]>> => {
    const response = await axiosClient.get<ApiResponse<Payment[]>>(`/payments/bill/${billId}`);
    return response.data;
  },

  verifyPayment: async (id: string | number, data: VerifyPaymentRequest): Promise<ApiResponse<Payment>> => {
    const response = await axiosClient.put<ApiResponse<Payment>>(`/payments/${id}/verify`, data);
    return response.data;
  },

  downloadReceipt: async (paymentId: string | number): Promise<Blob> => {
    const response = await axiosClient.get(`/payments/${paymentId}/receipt/pdf`, {
      responseType: 'blob',
    });
    return response.data;
  },
};
