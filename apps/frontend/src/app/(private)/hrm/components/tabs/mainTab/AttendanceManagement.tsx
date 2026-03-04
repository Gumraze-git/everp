'use client';
import SubNavigation from '@/app/components/common/SubNavigation';
import { ATTENDANCE_TABS } from '@/app/(private)/hrm/constants';

export default function AttendanceManagement() {
  return (
    <>
      <SubNavigation tabs={ATTENDANCE_TABS} />
    </>
  );
}
