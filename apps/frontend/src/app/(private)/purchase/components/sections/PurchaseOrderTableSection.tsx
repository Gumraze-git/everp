import React from 'react';

import { PurchaseOrder } from '@/app/(private)/purchase/types/PurchaseOrderType';
import { useRole } from '@/app/hooks/useRole';
import { formatDateTime } from '@/app/utils/date';
import IconButton from '@/app/components/common/IconButton';
import StatusLabel from '@/app/components/common/StatusLabel';

export interface PurchaseOrderTableProps {
  currentOrders: PurchaseOrder[];
  handleViewDetail: (orderId: string) => void;
  handleApprove: (orderId: string) => void;
  handleReject: (orderId: string) => void;
  handleDelivery: (orderId: string) => void;
}

export default function PurchaseOrderTable({
  currentOrders,
  handleViewDetail,
  handleApprove,
  handleReject,
  handleDelivery,
}: PurchaseOrderTableProps) {
  const role = useRole();
  const isSupplier = role === 'SUPPLIER_ADMIN';

  return (
    <div className="overflow-x-auto">
      <table className="w-full">
        <thead>
          <tr className="border-b border-gray-200">
            <th className="py-3 px-4 text-xs font-medium text-gray-500 uppercase tracking-wider">
              발주번호
            </th>
            <th className="py-3 px-4 text-xs font-medium text-gray-500 uppercase tracking-wider">
              공급업체
            </th>
            <th className="py-3 px-4 text-xs font-medium text-gray-500 uppercase tracking-wider">
              주문품목
            </th>
            <th className="py-3 px-4 text-xs font-medium text-gray-500 uppercase tracking-wider">
              총금액
            </th>
            <th className="py-3 px-4 text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-50">
              <div className="flex justify-center items-center space-x-1">
                <span>주문일자</span>
              </div>
            </th>
            <th className="py-3 px-4 text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-50">
              <div className="flex justify-center items-center space-x-1">
                <span>납기일</span>
              </div>
            </th>
            <th className="py-3 px-4 text-xs font-medium text-gray-500 uppercase tracking-wider">
              상태
            </th>
            <th className="py-3 px-4 text-xs font-medium text-gray-500 uppercase tracking-wider">
              작업
            </th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-200">
          {currentOrders.map((order) => (
            <tr
              key={order.purchaseOrderId}
              className="hover:bg-gray-50 transition-colors duration-200 text-center"
            >
              <td className="py-3 px-4 text-sm font-medium text-gray-900">
                {order.purchaseOrderNumber}
              </td>
              <td className="py-3 px-4 text-sm text-gray-900">{order.supplierName}</td>
              <td className="py-3 px-4 text-sm text-gray-900 max-w-xs truncate">
                {order.itemsSummary}
              </td>
              <td className="py-3 px-4 text-sm font-medium text-gray-900">{order.totalAmount}</td>
              <td className="py-3 px-4 text-sm text-gray-500">{formatDateTime(order.orderDate)}</td>
              <td className="py-3 px-4 text-sm text-gray-500">{formatDateTime(order.dueDate)}</td>
              <td className="py-3 px-4">
                <StatusLabel $statusCode={order.statusCode} />
              </td>
              <td className="py-3 px-4 text-center">
                <div className="flex items-center justify-center space-x-2">
                  <button
                    onClick={() => handleViewDetail(order.purchaseOrderId)}
                    className="text-blue-600 hover:text-blue-500 cursor-pointer"
                    title="상세보기"
                  >
                    <i className="ri-eye-line"></i>
                  </button>
                  {isSupplier && order.statusCode === 'PENDING' && (
                    <>
                      <button
                        onClick={() => handleApprove(order.purchaseOrderId)}
                        className="text-green-600 hover:text-green-900 cursor-pointer"
                        title="승인"
                      >
                        <i className="ri-check-line"></i>
                      </button>
                      <button
                        onClick={() => handleReject(order.purchaseOrderId)}
                        className="text-red-600 hover:text-red-900 cursor-pointer"
                        title="반려"
                      >
                        <i className="ri-close-line"></i>
                      </button>
                    </>
                  )}

                  {isSupplier && order.statusCode === 'APPROVAL' && (
                    <IconButton
                      size="sm"
                      variant="soft"
                      icon="ri-truck-line"
                      onClick={() => handleDelivery(order.purchaseOrderId)}
                    />
                  )}
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
