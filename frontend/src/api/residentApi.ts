import { axiosClient } from './axiosClient';
import { Resident, PageResponse, FamilyMember, Vehicle } from '../types';

export const residentApi = {
  search: async (params?: {
    buildingId?: string;
    active?: boolean;
    isOwner?: boolean;
    search?: string;
    page?: number;
    size?: number;
  }): Promise<PageResponse<Resident>> => {
    const res = await axiosClient.get<PageResponse<Resident>>('/residents', { params });
    return res.data;
  },

  getAllResidents: async (params?: {
    buildingId?: string;
    active?: boolean;
    isOwner?: boolean;
    search?: string;
    page?: number;
    size?: number;
  }): Promise<PageResponse<Resident>> => {
    return residentApi.search(params);
  },

  getById: async (id: string): Promise<Resident> => {
    const res = await axiosClient.get<Resident>(`/residents/${id}`);
    return res.data;
  },

  getMyProfile: async (): Promise<Resident> => {
    const res = await axiosClient.get<Resident>('/residents/me');
    return res.data;
  },

  create: async (data: any): Promise<Resident> => {
    const res = await axiosClient.post<Resident>('/residents', data);
    return res.data;
  },

  createResident: async (data: any): Promise<Resident> => {
    return residentApi.create(data);
  },

  update: async (id: string, data: any): Promise<Resident> => {
    const res = await axiosClient.put<Resident>(`/residents/${id}`, data);
    return res.data;
  },

  deactivate: async (id: string): Promise<Resident> => {
    const res = await axiosClient.patch<Resident>(`/residents/${id}/deactivate`);
    return res.data;
  },

  addFamilyMember: async (residentId: string, member: Partial<FamilyMember>): Promise<FamilyMember> => {
    const res = await axiosClient.post<FamilyMember>(`/residents/${residentId}/family-members`, member);
    return res.data;
  },

  removeFamilyMember: async (id: string): Promise<void> => {
    await axiosClient.delete(`/residents/family-members/${id}`);
  },

  addVehicle: async (residentId: string, vehicle: Partial<Vehicle>): Promise<Vehicle> => {
    const res = await axiosClient.post<Vehicle>(`/residents/${residentId}/vehicles`, vehicle);
    return res.data;
  },

  removeVehicle: async (id: string): Promise<void> => {
    await axiosClient.delete(`/residents/vehicles/${id}`);
  },
};
