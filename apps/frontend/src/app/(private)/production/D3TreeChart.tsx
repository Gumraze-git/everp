import React, { useEffect, useRef } from 'react';
import * as d3 from 'd3';
import { calculateOptimalTreeSize, TreeNode } from './utils/bomTreeUtils';

interface D3TreeChartProps {
  data: TreeNode;
  width?: number;
  height?: number;
}

// D3 HierarchyNode 타입 정의
type HierarchyNode = d3.HierarchyNode<TreeNode>;
type HierarchyPointNode = d3.HierarchyPointNode<TreeNode>;

const D3TreeChart: React.FC<D3TreeChartProps> = ({ data, width = 600, height = 400 }) => {
  const svgRef = useRef<SVGSVGElement>(null);

  useEffect(() => {
    if (!svgRef.current || !data) return;

    const svg = d3.select(svgRef.current);
    svg.selectAll('*').remove(); // 이전 차트 제거

    const margin = { top: 40, right: 20, bottom: 20, left: 40 };
    const innerWidth = width - margin.left - margin.right;
    const innerHeight = height - margin.top - margin.bottom;

    // 동적 크기 계산
    const {
      width: optimalWidth,
      height: optimalHeight,
      scale: optimalScale,
    } = calculateOptimalTreeSize(data, innerWidth, innerHeight);

    // 트리 레이아웃 생성 (동적 크기 적용)
    const treemap = d3.tree<TreeNode>().size([optimalWidth, optimalHeight]);

    // 데이터를 트리 구조로 변환
    const root: HierarchyNode = d3.hierarchy(data);
    const treeData: HierarchyPointNode = treemap(root);

    // 루트 노드를 최상단 가운데로 이동
    const rootNode = treeData;
    rootNode.x = optimalWidth / 2;
    rootNode.y = 50; // 최상단에서 50px 아래

    // 배경 격자 패턴 추가
    const defs = svg.append('defs');
    const pattern = defs
      .append('pattern')
      .attr('id', 'grid')
      .attr('width', 20)
      .attr('height', 20)
      .attr('patternUnits', 'userSpaceOnUse');

    pattern
      .append('path')
      .attr('d', 'M 20 0 L 0 0 0 20')
      .style('fill', 'none')
      .style('stroke', '#f3f4f6')
      .style('stroke-width', 1);

    svg.append('rect').attr('width', '100%').attr('height', '100%').style('fill', 'url(#grid)');

    // 메인 그룹 컨테이너 생성
    const mainGroup = svg.append('g');

    // 줌 동작 정의 (mainGroup 생성 후)
    const zoom = d3
      .zoom<SVGSVGElement, unknown>()
      .scaleExtent([0.1, 3])
      .on('zoom', (event: d3.D3ZoomEvent<SVGSVGElement, unknown>) => {
        mainGroup.attr('transform', event.transform.toString());
      });

    // 줌 적용
    svg.call(zoom);

    // 초기 줌 설정 (루트가 중앙 상단에 오도록)
    const initialTransform = d3.zoomIdentity.translate(-180, 20).scale(0.7);
    svg.call(zoom.transform, initialTransform);

    // 차트 콘텐츠 그룹
    const g = mainGroup.append('g');

    // 링크(연결선) 생성 (간단한 직선)
    g.selectAll('.link')
      .data(treeData.descendants().slice(1))
      .enter()
      .append('line')
      .attr('class', 'link')
      .attr('x1', (d: HierarchyPointNode) => d.parent!.x)
      .attr('y1', (d: HierarchyPointNode) => d.parent!.y + 30)
      .attr('x2', (d: HierarchyPointNode) => d.x)
      .attr('y2', (d: HierarchyPointNode) => d.y - 30)
      .style('stroke', '#9ca3af')
      .style('stroke-width', '2px');

    // 노드 그룹 생성 (세로 방향)
    const node = g
      .selectAll('.node')
      .data(treeData.descendants())
      .enter()
      .append('g')
      .attr(
        'class',
        (d: HierarchyPointNode) => `node ${d.children ? 'node--internal' : 'node--leaf'}`,
      )
      .attr('transform', (d: HierarchyPointNode) => `translate(${d.x},${d.y})`);

    // 노드 배경 사각형 (높이를 늘려서 텍스트 줄바꿈 공간 확보)
    node
      .append('rect')
      .attr('x', -70)
      .attr('y', -30)
      .attr('width', 140)
      .attr('height', 60)
      .attr('rx', 8)
      .style('fill', (d: HierarchyPointNode) => {
        if (d.depth === 0) return '#ef4444'; // 루트 노드 - red-500
        if (d.depth === 1) return '#3b82f6'; // Level 1 - blue-500
        if (d.depth === 2) return '#10b981'; // Level 2 - green-500
        return '#f59e0b'; // Level 3+ - yellow-500
      })
      .style('stroke', '#fff')
      .style('stroke-width', '2px')
      .style('filter', 'drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1))');

    // 노드 텍스트 - 이름만 표시 (코드와 ID 제거)
    node.each(function (d: HierarchyPointNode) {
      const textElement = d3.select(this);
      const name = d.data.name;
      const maxCharsPerLine = 12; // 한 줄당 최대 글자 수

      if (name.length <= maxCharsPerLine) {
        // 짧은 이름은 한 줄로 (코드가 없으므로 중앙에 배치)
        textElement
          .append('text')
          .attr('dy', -5)
          .attr('text-anchor', 'middle')
          .style('fill', 'white')
          .style('font-size', '12px')
          .style('font-weight', '600')
          .style('text-shadow', '0 1px 2px rgba(0,0,0,0.3)')
          .text(name);
      } else {
        // 긴 이름은 두 줄로 나누기
        const firstLine = name.substring(0, maxCharsPerLine);
        const secondLine = name.substring(maxCharsPerLine);

        textElement
          .append('text')
          .attr('dy', -10)
          .attr('text-anchor', 'middle')
          .style('fill', 'white')
          .style('font-size', '11px')
          .style('font-weight', '600')
          .style('text-shadow', '0 1px 2px rgba(0,0,0,0.3)')
          .text(firstLine);

        if (secondLine) {
          textElement
            .append('text')
            .attr('dy', 2)
            .attr('text-anchor', 'middle')
            .style('fill', 'white')
            .style('font-size', '11px')
            .style('font-weight', '600')
            .style('text-shadow', '0 1px 2px rgba(0,0,0,0.3)')
            .text(
              secondLine.length > maxCharsPerLine
                ? secondLine.substring(0, maxCharsPerLine - 1) + '…'
                : secondLine,
            );
        }
      }
    });

    // 수량 텍스트 (코드 제거로 위치 조정)
    node
      .filter((d: HierarchyPointNode) => d.data.quantity !== undefined)
      .append('text')
      .attr('dy', 16)
      .attr('text-anchor', 'middle')
      .style('fill', 'white')
      .style('font-size', '9px')
      .style('font-weight', '600')
      .style('text-shadow', '0 1px 2px rgba(0,0,0,0.3)')
      .text((d: HierarchyPointNode) => `${d.data.quantity}${d.data.unit || ''}`);

    // 레벨 레이블 제거 (아래 코드 전체 삭제)
    // const fixedLabelsGroup = svg.append('g').attr('class', 'fixed-labels');
    // ...
  }, [data, width, height]);

  return (
    <div className="w-full bg-white rounded-lg border border-gray-200 p-6 relative">
      <div className="mb-4">
        <div className="flex items-center justify-between">
          <h4 className="text-lg font-semibold text-gray-900">BOM 레벨 구조</h4>
          <div className="text-xs text-gray-500 bg-gray-50 px-3 py-1 rounded-lg">
            💡 마우스 휠로 확대/축소, 드래그로 이동 가능
          </div>
        </div>
        <div className="flex items-center space-x-4 text-sm text-gray-600 mt-2">
          <div className="flex items-center space-x-2">
            <div className="w-3 h-3 bg-red-500 rounded"></div>
            <span>완제품</span>
          </div>
          <div className="flex items-center space-x-2">
            <div className="w-3 h-3 bg-blue-500 rounded"></div>
            <span>Level 1</span>
          </div>
          <div className="flex items-center space-x-2">
            <div className="w-3 h-3 bg-green-500 rounded"></div>
            <span>Level 2</span>
          </div>
          <div className="flex items-center space-x-2">
            <div className="w-3 h-3 bg-yellow-500 rounded"></div>
            <span>Level 3+</span>
          </div>
        </div>
      </div>
      <div className="overflow-x-auto bg-gray-50 rounded-lg p-4">
        <svg
          ref={svgRef}
          width={width}
          height={height}
          className="border border-gray-200 rounded-lg bg-white shadow-sm"
        />
      </div>
    </div>
  );
};

export default D3TreeChart;
