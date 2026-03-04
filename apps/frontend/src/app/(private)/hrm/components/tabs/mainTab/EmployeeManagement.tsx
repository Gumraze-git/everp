'use client';

import SubNavigation from '@/app/components/common/SubNavigation';
import { HR_TABS } from '@/app/(private)/hrm/constants';

export default function EmployeeManagement() {
  return (
    <>
      <SubNavigation tabs={HR_TABS} />
    </>
  );
}
