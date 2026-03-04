import { useCallback, useState } from 'react';
import { useDebounce } from 'use-debounce';

export function useDebouncedKeyword(delay = 200) {
  const [keyword, setKeyword] = useState('');
  const [debouncedKeyword] = useDebounce(keyword, delay);

  const handleKeywordChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    setKeyword(e.target.value);
  }, []);

  const resetKeyword = useCallback(() => setKeyword(''), []);

  return {
    keyword,
    debouncedKeyword,
    setKeyword,
    handleKeywordChange,
    resetKeyword,
  };
}
