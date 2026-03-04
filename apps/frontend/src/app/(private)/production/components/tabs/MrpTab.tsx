'use client';

import { MRP_TABS } from '@/app/(private)/production/constants';
import SubNavigation from '@/app/components/common/SubNavigation';

export default function MrpTab() {
  return (
    <>
      <SubNavigation tabs={MRP_TABS} />
    </>
  );
}
