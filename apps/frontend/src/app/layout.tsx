import type { Metadata } from 'next';
import { Geist, Geist_Mono, Noto_Sans_KR, Pacifico } from 'next/font/google';
import '../styles/globals.css';
import Providers from '@/app/providers';

const geistSans = Geist({
  display: 'swap',
  variable: '--font-geist-sans',
});

const geistMono = Geist_Mono({
  display: 'swap',
  variable: '--font-geist-mono',
});

const pacifico = Pacifico({
  weight: '400',
  display: 'swap',
  variable: '--font-pacifico',
});

const notoSansKR = Noto_Sans_KR({
  subsets: ['latin'],
  weight: ['100', '200', '300', '400', '500', '600', '700', '800', '900'],
  variable: '--font-noto-sans-kr',
  display: 'swap',
});

export const metadata: Metadata = {
  title: 'EvErp',
  description: '기업 자원 관리 통합 시스템',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html
      lang="ko"
      className={`${geistSans.variable} ${geistMono.variable} ${pacifico.variable} ${notoSansKR.variable}`}
    >
      <body>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
