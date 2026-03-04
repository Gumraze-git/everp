import Image from 'next/image';
import Link from 'next/link';

export default function Logo() {
  return (
    <Link href="/dashboard" className="pl-4 flex items-center space-x-2 cursor-pointer">
      {/* <div className="w-8 h-8 bg-gray-800 rounded-lg flex items-center justify-center">
        <i className="ri-building-4-line text-white text-lg"></i>
      </div>
      <div className="pt-0.5">
        <span className="text-xl font-black text-gray-800">EvERP</span>
      </div> */}
      <Image src="/images/everp_logo.png" alt="everp 로고" width={76} height={28} />
    </Link>
  );
}
