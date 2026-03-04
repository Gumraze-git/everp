import { useQuery } from '@tanstack/react-query';
import { KeyValueItem } from '@/app/types/CommonType';
import { useMemo } from 'react';

type AllOptionMode = 'include' | 'exclude' | 'as-is';

export const useDropdown = (
  key: string,
  fetchFn: () => Promise<KeyValueItem[]>,
  mode: AllOptionMode = 'as-is',
) => {
  const { data, isLoading, isError } = useQuery<KeyValueItem[]>({
    queryKey: [key],
    queryFn: fetchFn,
    staleTime: Infinity,
  });

  const filteredData = useMemo(() => {
    if (!data) return [];

    let list = [...data];

    switch (mode) {
      case 'include':
        const hasAll = list.some((item) => item.value === 'value');
        if (!hasAll) {
          list = [{ key: '', value: '전체' }, ...list];
        }
        break;
      case 'exclude':
        list = list.filter((item) => item.value !== 'ALL');
        break;
      case 'as-is':
      default:
        break;
    }

    return list;
  }, [data, mode]);

  return { options: filteredData ?? [], isLoading, isError };
};
