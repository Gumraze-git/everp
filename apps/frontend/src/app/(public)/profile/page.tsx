'use client';

import { useState } from 'react';

import AttendanceRecord from './components/AttendanceRecord';
import ProfileHeader from './components/ProfileHeader';
import TrainingStatus from './components/TrainingStatus';
import EmploymentInfo from './components/EmploymentInfo';

export default function ProfilePage() {
  const [isEditing, setIsEditing] = useState(false);

  return (
    <div className="min-h-screen bg-gray-50">
      <ProfileHeader $isEditing={isEditing} $setIsEditing={setIsEditing} />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="space-y-8">
          <EmploymentInfo $isEditing={isEditing} $setIsEditing={setIsEditing} />
          <div className="grid grid-cols-1 gap-4 items-start lg:grid-cols-[22rem_1fr]">
            <div className="lg:sticky lg:top-8 lg:h-fit">
              <AttendanceRecord />
            </div>
            <div>
              <TrainingStatus />
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
