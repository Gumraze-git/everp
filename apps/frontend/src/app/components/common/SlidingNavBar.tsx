import { useRef, useEffect, useState } from 'react';
import { KeyValueItem } from '@/app/types/CommonType';

interface SlidingNavBarProps {
  items: KeyValueItem[];
  selectedKey: string;
  onSelect: (key: string) => void;
}

export default function SlidingNavBar({ items, selectedKey, onSelect }: SlidingNavBarProps) {
  const [indicatorStyle, setIndicatorStyle] = useState({});
  const navRefs = useRef<(HTMLButtonElement | null)[]>([]);

  useEffect(() => {
    const selectedIndex = items.findIndex((item) => item.key === selectedKey);
    const activeButton = navRefs.current[selectedIndex];

    if (activeButton) {
      setIndicatorStyle({
        left: `${activeButton.offsetLeft}px`,
        width: `${activeButton.offsetWidth}px`,
      });
    }
  }, [selectedKey, items]);

  return (
    <div
      className={`relative inline-flex max-h-12 gap-1 items-center bg-white rounded-lg border border-gray-300 p-1`}
    >
      {/* 슬라이딩 인디케이터 */}
      <div
        className="absolute top-1 bottom-1 bg-blue-500 rounded-md transition-all duration-300 ease-out pointer-events-none"
        style={indicatorStyle}
      />

      {/* 버튼들 */}
      <div className=" flex gap-1">
        {items.map((item, index) => (
          <button
            key={item.key}
            ref={(el) => {
              navRefs.current[index] = el;
            }}
            onClick={() => onSelect(item.key)}
            className={`font-medium transition-colors duration-200 whitespace-nowrap cursor-pointer z-10 rounded-md px-4 py-2 text-sm ${
              selectedKey === item.key ? 'text-white' : 'text-gray-700 hover:text-gray-900'
            }`}
          >
            {item.value}
          </button>
        ))}
      </div>
    </div>
  );
}
