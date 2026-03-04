// BOM 구성품 (Component)
export interface BomComponent {
  itemId: string;
  code: string;
  name: string;
  quantity: number;
  unit: string;
  level: string; // 예: "Level 1"
  supplierName: string;
  operationId: string;
  operationName: string;
  componentType: string;
}

// BOM 레벨 구조 노드 (새로운 트리 구조)
export interface BomLevelStructureNode {
  id: string;
  code: string;
  name: string;
  quantity: number | null;
  unit: string | null;
  level: number;
  parentId: string | null;
}

// 공정(Routing) 정보
export interface BomRouting {
  itemName: string;
  sequence: number;
  operationName: string;
  runTime: number; // 단위 시간 (예: 분)
}

// BOM 전체 정보
export interface BomDetailResponse {
  bomId: string;
  bomNumber: string;
  productId: string;
  productNumber: string;
  productName: string;
  version: string;
  statusCode: string; // 예: "활성"
  lastModifiedAt: string;
  components: BomComponent[];
  levelStructure: BomLevelStructureNode[];
  routing: BomRouting[];
}
