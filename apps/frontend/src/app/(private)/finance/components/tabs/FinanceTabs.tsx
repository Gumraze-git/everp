'use client';

import TabNavigation from '@/app/components/common/TabNavigation';
import { useRole } from '@/app/hooks/useRole';
import { getFinanceTabsByRole } from '../../utils';

export default function FinanceTabs() {
  const role = useRole();
  //   const role = 'CUSTOMER_ADMIN';
  //   const role = 'SUPPLIER_ADMIN';
  const allowedTabs = getFinanceTabsByRole(role as string);

  return <TabNavigation tabs={allowedTabs} />;
}
