import path from 'path';
import type { NextConfig } from 'next';

const nextConfig: NextConfig = {
  reactStrictMode: true, // 리액트 스트릭트 모드 (개발 시 문제 탐지)
  eslint: {
    ignoreDuringBuilds: true,
  },
  compiler: {},
  images: {
    domains: ['example.com'], // 외부 이미지 도메인 허용
  },
  webpack(config) {
    // alias 설정
    config.resolve = config.resolve || {};
    config.resolve.alias = {
      ...(config.resolve.alias || {}),
      '@': path.resolve(__dirname, 'src'), // tsconfig랑 동일하게 src 기준
    };
    return config;
  },
};

export default nextConfig;
