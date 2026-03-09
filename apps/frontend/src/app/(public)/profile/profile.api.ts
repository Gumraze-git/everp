import axios from '@/lib/axiosInstance';
import {
  AttendanceRecordsResponse,
  EditUserRequest,
  ProfileInfoResponse,
  RequestVacation,
  TodayAttendResponse,
  TrainingResponse,
} from './ProfileType';
import { PROFILE_ENDPOINTS } from '@/app/types/api';

export const postVacation = async (items: RequestVacation): Promise<void> => {
  await axios.post(PROFILE_ENDPOINTS.VACATION, items);
};

export const postTraining = async (trainingId: string): Promise<void> => {
  await axios.post(PROFILE_ENDPOINTS.REGISTER_TRAINING(trainingId));
};

export const patchCheckIn = async (): Promise<void> => {
  await axios.patch(PROFILE_ENDPOINTS.CHECK_IN);
};

export const patchCheckout = async (): Promise<void> => {
  await axios.patch(PROFILE_ENDPOINTS.CHECK_OUT);
};

export const getProfile = async (): Promise<ProfileInfoResponse> => {
  const res = await axios.get<ProfileInfoResponse>(PROFILE_ENDPOINTS.PROFILE_INFO);
  return res.data;
};

export const getTodayAttendance = async (): Promise<TodayAttendResponse> => {
  const res = await axios.get<TodayAttendResponse>(PROFILE_ENDPOINTS.TODAY_ATTENDANCE);
  return res.data;
};

export const getAttendaceRecords = async (): Promise<AttendanceRecordsResponse[]> => {
  const res = await axios.get<AttendanceRecordsResponse[]>(PROFILE_ENDPOINTS.ATTENDANCE_RECORDS);
  return res.data;
};

export const getAvailableTraining = async (): Promise<TrainingResponse[]> => {
  const res = await axios.get<TrainingResponse[]>(PROFILE_ENDPOINTS.AVAILABLE_TRAINING);
  return res.data;
};

export const getProgressTraining = async (): Promise<TrainingResponse[]> => {
  const res = await axios.get<TrainingResponse[]>(PROFILE_ENDPOINTS.PROGRESS_TRAINING);
  return res.data;
};
export const getCompletedTraining = async (): Promise<TrainingResponse[]> => {
  const res = await axios.get<TrainingResponse[]>(PROFILE_ENDPOINTS.COMPLETED_TRAINING);
  return res.data;
};

export const postProfile = async (profileInfo: EditUserRequest): Promise<void> => {
  await axios.patch(PROFILE_ENDPOINTS.EDIT_PROFILE, {
    phoneNumber: profileInfo.phoneNumber,
    address: profileInfo.address,
  });
};
