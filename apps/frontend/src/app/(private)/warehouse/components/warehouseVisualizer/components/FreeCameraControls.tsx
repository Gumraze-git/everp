import { useFrame } from '@react-three/fiber';
import { useRef, useEffect } from 'react';
import * as THREE from 'three';
import { PointerLockControls } from '@react-three/drei';
import { PointerLockControls as PointerLockControlsImpl } from 'three-stdlib';

const FreeCameraControls = () => {
  const controlsRef = useRef<PointerLockControlsImpl | null>(null);
  const direction = new THREE.Vector3();
  const speed = 10;
  const keys = useRef({ w: false, a: false, s: false, d: false });

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.code.startsWith('Key')) {
        keys.current[e.code.charAt(3).toLowerCase() as 'w' | 'a' | 's' | 'd'] = true;
      }
    };
    const handleKeyUp = (e: KeyboardEvent) => {
      if (e.code.startsWith('Key')) {
        keys.current[e.code.charAt(3).toLowerCase() as 'w' | 'a' | 's' | 'd'] = false;
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    window.addEventListener('keyup', handleKeyUp);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      window.removeEventListener('keyup', handleKeyUp);
    };
  }, []);

  useFrame((_, delta) => {
    if (!controlsRef.current?.getObject) return;
    const moveSpeed = speed * delta;
    direction.set(0, 0, 0);
    if (keys.current.w) direction.z -= moveSpeed;
    if (keys.current.s) direction.z += moveSpeed;
    if (keys.current.a) direction.x -= moveSpeed;
    if (keys.current.d) direction.x += moveSpeed;
    const cam = controlsRef.current.getObject();
    cam.translateX(direction.x);
    cam.translateZ(direction.z);
  });

  return <PointerLockControls ref={controlsRef} />;
};

export default FreeCameraControls;
