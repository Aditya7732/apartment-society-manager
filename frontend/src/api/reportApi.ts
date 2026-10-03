import { axiosClient } from './axiosClient';

export const reportApi = {
  // Financial Maintenance Collection Report
  downloadFinancialCsv: async (startDate?: string, endDate?: string): Promise<Blob> => {
    const response = await axiosClient.get('/reports/financial/csv', {
      params: { startDate, endDate },
      responseType: 'blob',
    });
    return response.data;
  },
  downloadFinancialPdf: async (startDate?: string, endDate?: string): Promise<Blob> => {
    const response = await axiosClient.get('/reports/financial/pdf', {
      params: { startDate, endDate },
      responseType: 'blob',
    });
    return response.data;
  },

  // Resident Occupancy Report
  downloadResidentsCsv: async (): Promise<Blob> => {
    const response = await axiosClient.get('/reports/residents/csv', {
      responseType: 'blob',
    });
    return response.data;
  },
  downloadResidentsPdf: async (): Promise<Blob> => {
    const response = await axiosClient.get('/reports/residents/pdf', {
      responseType: 'blob',
    });
    return response.data;
  },

  // Helpdesk & Complaints SLA Report
  downloadComplaintsCsv: async (startDate?: string, endDate?: string): Promise<Blob> => {
    const response = await axiosClient.get('/reports/complaints/csv', {
      params: { startDate, endDate },
      responseType: 'blob',
    });
    return response.data;
  },
  downloadComplaintsPdf: async (startDate?: string, endDate?: string): Promise<Blob> => {
    const response = await axiosClient.get('/reports/complaints/pdf', {
      params: { startDate, endDate },
      responseType: 'blob',
    });
    return response.data;
  },

  // Society Expenses Ledger Report
  downloadExpensesCsv: async (startDate?: string, endDate?: string): Promise<Blob> => {
    const response = await axiosClient.get('/reports/expenses/csv', {
      params: { startDate, endDate },
      responseType: 'blob',
    });
    return response.data;
  },
  downloadExpensesPdf: async (startDate?: string, endDate?: string): Promise<Blob> => {
    const response = await axiosClient.get('/reports/expenses/pdf', {
      params: { startDate, endDate },
      responseType: 'blob',
    });
    return response.data;
  },

  // Overdue Defaulters Report
  downloadDefaultersCsv: async (): Promise<Blob> => {
    const response = await axiosClient.get('/reports/defaulters/csv', {
      responseType: 'blob',
    });
    return response.data;
  },
  downloadDefaultersPdf: async (): Promise<Blob> => {
    const response = await axiosClient.get('/reports/defaulters/pdf', {
      responseType: 'blob',
    });
    return response.data;
  },

  // Compatibility aliases for existing usages
  downloadCollectionReport: async (startDate?: string, endDate?: string): Promise<Blob> => {
    return reportApi.downloadFinancialCsv(startDate, endDate);
  },
  downloadDefaultersReport: async (): Promise<Blob> => {
    return reportApi.downloadDefaultersCsv();
  },
  downloadExpenseReport: async (startDate?: string, endDate?: string): Promise<Blob> => {
    return reportApi.downloadExpensesCsv(startDate, endDate);
  },
  downloadComplaintsReport: async (startDate?: string, endDate?: string): Promise<Blob> => {
    return reportApi.downloadComplaintsCsv(startDate, endDate);
  },
};

export default reportApi;

