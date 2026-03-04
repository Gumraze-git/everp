interface PageHeaderProps {
  title: string;
  subTitle?: string;
}

export default function PageTitle({ title, subTitle }: PageHeaderProps) {
  return (
    <div className="mb-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">{title}</h1>
          <p className="text-gray-500 text-sm mt-1">{subTitle}</p>
        </div>
      </div>
    </div>
  );
}
