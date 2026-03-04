'use client';

import { useState } from 'react';
import { KeyValueItem } from '@/app/types/CommonType';
import IconButton from '@/app/components/common/IconButton';

interface InputListProps<T extends string | number = string> {
  initialItems?: KeyValueItem<T>[];
  onChange?: (items: KeyValueItem<T>[]) => void;
}

export default function InputList<T extends string | number = string>({
  initialItems = [],
  onChange,
}: InputListProps<T>) {
  const [items, setItems] = useState<KeyValueItem<T>[]>(initialItems);

  // 값 변경
  const handleChange = (key: T, value: string) => {
    const newItems = items.map((item) => (item.key === key ? { ...item, value } : item));
    setItems(newItems);
    onChange?.(newItems);
  };

  // 새로운 항목 추가
  const handleAdd = () => {
    const newKey: T = (
      items.length ? (((items[items.length - 1].key as number) + 1) as T) : 1
    ) as T; // 숫자 키 기준 자동 증가
    const newItem: KeyValueItem<T> = {
      key: newKey,
      value: '',
    };
    const newItems = [...items, newItem];
    setItems(newItems);
    onChange?.(newItems);
  };

  // 항목 삭제
  const handleRemove = (key: T) => {
    const newItems = items.filter((item) => item.key !== key);
    setItems(newItems);
    onChange?.(newItems);
  };

  return (
    <div className="space-y-0.5">
      <div className="flex justify-between items-center px-2 pb-3">
        <h1>공정 순서 입력</h1>
        <IconButton
          label="공정 추가"
          variant="secondary"
          icon="ri-add-line"
          size="sm"
          onClick={handleAdd}
        />
      </div>

      {items.map((item, index) => (
        <div key={item.key as string | number} className="flex items-center">
          <span className="w-5 flex justify-center">{index + 1}</span>
          <input
            type="text"
            value={item.value}
            onChange={(e) => handleChange(item.key, e.target.value)}
            className="h-8 ml-3 border p-2 rounded"
          />
          <IconButton
            icon="ri-close-line"
            label=""
            className="bg-white !text-gray-400"
            size="lg"
            onClick={() => handleRemove(item.key)}
          />
        </div>
      ))}
    </div>
  );
}
