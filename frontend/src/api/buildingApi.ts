import axiosClient from './axiosClient';
import { Building, ApiResponse } from '../types';

export interface CreateBuildingData {
  name: string;
  code?: string;
  totalFloors: number;
  totalFlats?: number;
  description?: string;
}

export const buildingApi = {
  getAllBuildings: async (): Promise<ApiResponse<Building[]>> => {
    const response = await axiosClient.get<ApiResponse<Building[]>>('/buildings');
    return response.data;
  },

  getBuildingById: async (id: number | string): Promise<ApiResponse<Building>> => {
    const response = await axiosClient.get<ApiResponse<Building>>(`/buildings/${id}`);
    return response.data;
  },

  createBuilding: async (data: CreateBuildingData): Promise<ApiResponse<Building>> => {
    const floors = Number(data.totalFloors) || 1;
    const code = data.code || 'BLK-' + (data.name || '').trim().toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 8);
    const payload = {
      name: data.name,
      code: code || 'BLK-' + Date.now(),
      totalFloors: floors,
      totalFlats: data.totalFlats || (floors * 4),
      description: data.description || '',
    };
    const response = await axiosClient.post<ApiResponse<Building>>('/buildings', payload);
    return response.data;
  },

  // Alias methods for compatibility
  getAll: async (): Promise<Building[]> => {
    const res = await axiosClient.get<ApiResponse<Building[]>>('/buildings');
    return res.data.data || (res.data as any);
  },
};
