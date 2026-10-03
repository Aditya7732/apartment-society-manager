import { axiosClient } from './axiosClient';
import { Flat, PageResponse, OccupancyStatus } from '../types';

export const flatApi = {
  search: async (params?: {
    buildingId?: string;
    floorNumber?: number;
    status?: OccupancyStatus;
    search?: string;
    page?: number;
    size?: number;
  }): Promise<PageResponse<Flat>> => {
    const res = await axiosClient.get<PageResponse<Flat>>('/flats', { params });
    return res.data;
  },

  getAllFlats: async (params?: {
    buildingId?: string;
    floorNumber?: number;
    status?: OccupancyStatus;
    search?: string;
    page?: number;
    size?: number;
  }): Promise<PageResponse<Flat>> => {
    return flatApi.search(params);
  },

  getById: async (id: string): Promise<Flat> => {
    const res = await axiosClient.get<Flat>(`/flats/${id}`);
    return res.data;
  },

  getByBuilding: async (buildingId: string): Promise<Flat[]> => {
    const res = await axiosClient.get<Flat[]>(`/flats/building/${buildingId}`);
    return res.data;
  },

  create: async (data: Partial<Flat>): Promise<Flat> => {
    const res = await axiosClient.post<Flat>('/flats', data);
    return res.data;
  },

  createFlat: async (data: Partial<Flat>): Promise<Flat> => {
    return flatApi.create(data);
  },

  update: async (id: string, data: Partial<Flat>): Promise<Flat> => {
    const res = await axiosClient.put<Flat>(`/flats/${id}`, data);
    return res.data;
  },

  updateOccupancyStatus: async (id: string, status: OccupancyStatus): Promise<Flat> => {
    const res = await axiosClient.patch<Flat>(`/flats/${id}/occupancy-status?status=${status}`);
    return res.data;
  },

  delete: async (id: string): Promise<void> => {
    await axiosClient.delete(`/flats/${id}`);
  },
};
