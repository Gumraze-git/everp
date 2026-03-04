'use client';
import SubNavigation from '@/app/components/common/SubNavigation';
import { TRAINING_TABS } from '@/app/(private)/hrm/constants';

export default function TrainingManagement() {
  return (
    <>
      <SubNavigation tabs={TRAINING_TABS} />
    </>
  );
}
