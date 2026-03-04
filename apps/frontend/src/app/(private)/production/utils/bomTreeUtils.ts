// BOM 데이터를 트리 구조로 변환하는 유틸리티

export interface BomApiNode {
  id: string;
  code: string;
  name: string;
  quantity?: number;
  unit?: string;
  level: number;
  parentId?: string;
  children?: BomApiNode[];
}

export interface TreeNode {
  id: string;
  name: string;
  code: string;
  quantity?: number;
  unit?: string;
  level: number;
  children?: TreeNode[];
}

/**
 * 백엔드 BOM 데이터를 D3 트리 구조로 변환
 * @param bomData 백엔드에서 받아온 BOM 데이터
 * @returns D3TreeChart에서 사용할 수 있는 트리 구조
 */
export function transformBomDataToTree(bomData: BomApiNode[]): TreeNode | null {
  if (!bomData || bomData.length === 0) return null;

  // 1. 루트 노드 찾기 (level이 0이거나 parentId가 null/undefined인 노드)
  const rootNode = bomData.find(
    (node) => node.level === 0 || !node.parentId || node.parentId === null,
  );
  if (!rootNode) return null;

  // 2. 재귀적으로 자식 노드들을 연결
  function buildTree(parentNode: BomApiNode): TreeNode {
    const children = bomData
      .filter((node) => node.parentId === parentNode.id)
      .map((child) => buildTree(child))
      .sort((a, b) => a.code.localeCompare(b.code)); // 코드 순으로 정렬

    return {
      id: parentNode.id,
      name: parentNode.name,
      code: parentNode.code,
      quantity: parentNode.quantity,
      unit: parentNode.unit,
      level: parentNode.level,
      children: children.length > 0 ? children : undefined,
    };
  }

  return buildTree(rootNode);
}

/**
 * 플랫한 BOM 리스트를 계층 구조로 변환
 * (백엔드에서 플랫한 리스트로 데이터를 주는 경우)
 */
export function transformFlatBomToTree(flatBomData: BomApiNode[]): TreeNode | null {
  if (!flatBomData || flatBomData.length === 0) return null;

  // ID별로 맵핑
  const nodeMap = new Map<string, BomApiNode>();
  flatBomData.forEach((node) => {
    nodeMap.set(node.id, { ...node, children: [] });
  });

  let root: BomApiNode | null = null;

  // 부모-자식 관계 설정
  flatBomData.forEach((node) => {
    if (!node.parentId || node.level === 0) {
      root = nodeMap.get(node.id) || null;
    } else {
      const parent = nodeMap.get(node.parentId);
      const child = nodeMap.get(node.id);
      if (parent && child) {
        if (!parent.children) parent.children = [];
        parent.children.push(child);
      }
    }
  });

  if (!root) return null;

  // TreeNode 형식으로 변환
  function convertToTreeNode(apiNode: BomApiNode): TreeNode {
    return {
      id: apiNode.id,
      name: apiNode.name,
      code: apiNode.code,
      quantity: apiNode.quantity,
      unit: apiNode.unit,
      level: apiNode.level,
      children: apiNode.children?.map((child) => convertToTreeNode(child)),
    };
  }

  return convertToTreeNode(root);
}

/**
 * 트리의 최대 깊이 계산 (동적 크기 조정을 위해)
 */
export function calculateTreeDepth(node: TreeNode): number {
  if (!node.children || node.children.length === 0) return 1;
  return 1 + Math.max(...node.children.map((child) => calculateTreeDepth(child)));
}

/**
 * 트리의 최대 너비 계산 (같은 레벨의 최대 노드 수)
 */
export function calculateTreeWidth(node: TreeNode): number {
  const levelCounts: number[] = [];

  function countNodesAtLevel(node: TreeNode, level: number) {
    if (!levelCounts[level]) levelCounts[level] = 0;
    levelCounts[level]++;

    if (node.children) {
      node.children.forEach((child) => countNodesAtLevel(child, level + 1));
    }
  }

  countNodesAtLevel(node, 0);
  return Math.max(...levelCounts);
}

/**
 * 동적 트리 크기 계산
 */
export function calculateOptimalTreeSize(
  treeData: TreeNode,
  baseWidth: number,
  baseHeight: number,
) {
  const depth = calculateTreeDepth(treeData);
  const width = calculateTreeWidth(treeData);

  // 깊이와 너비에 따라 동적으로 크기 조정
  const widthMultiplier = Math.max(1.5, width * 0.3);
  const heightMultiplier = Math.max(0.8, depth * 0.2);

  return {
    width: baseWidth * widthMultiplier,
    height: baseHeight * heightMultiplier,
    scale: Math.min(1, 1 / Math.max(widthMultiplier / 2, heightMultiplier / 2)), // 초기 스케일 조정
  };
}
