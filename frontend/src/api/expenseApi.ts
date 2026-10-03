import { axiosClient } from './axiosClient';
import { SocietyExpense, ApiResponse, PageResponse } from '../types';

export interface CreateExpenseRequest {
  title: string;
  category: string;
  amount: number;
  expenseDate: string;
  vendorName?: string;
  invoiceNumber?: string;
  description?: string;
}

export const expenseApi = {
  createExpense: async (data: CreateExpenseRequest): Promise<ApiResponse<SocietyExpense>> => {
    const response = await axiosClient.post<ApiResponse<SocietyExpense>>('/expenses', data);
    return response.data;
  },

  getAllExpenses: async (params?: { page?: number; size?: number; category?: string; fromDate?: string; toDate?: string }): Promise<PageResponse<SocietyExpense>> => {
    const response = await axiosClient.get<PageResponse<SocietyExpense>>('/expenses', { params });
    return response.data;
  },

  getExpenseById: async (id: string | number): Promise<SocietyExpense> => {
    const response = await axiosClient.get<SocietyExpense>(`/expenses/${id}`);
    return response.data;
  },

  deleteExpense: async (id: string | number): Promise<void> => {
    await axiosClient.delete(`/expenses/${id}`);
  },
};
