// src/api/plot.js
import request from '@/utils/request'; // 引入封装好的 request 实例

/**
 * 查询地块信息
 * @param {object} params - 查询参数
 * @param {number} [params.currentPage=1] - 当前页数
 * @param {number} [params.pageSize=10] - 每页条目数
 * @param {string} [params.plotNumber] - 地块编号 (前端查询字段，后端可能映射为 id 或其他)
 * @param {string} [params.responsiblePerson] - 地块负责人 (前端查询字段，后端可能映射为 username)
 * @returns {Promise<{ list: Array, total: number }>} 接口响应数据
 */
export const fetchPlotListApi = async (params = {}) => {
  try {
    const response = await request.get('/data_management/plot', {
      params: {
        page: params.currentPage || 1,
        pageSize: params.pageSize || 10,
        plotNumber: params.plotNumber,
        username: params.responsiblePerson
      }
    });

    if (response.code === 1) {
      // *** 重点调整：根据新的后端响应结构解析数据 ***
      const listData = response.data.rows || []; // 后端返回的数据列表在 data.rows
      const totalCount = response.data.total || 0; // 总数在 data.total
      return { list: listData, total: totalCount }; // 返回统一的 list 和 total 结构
    } else {
      throw new Error(response.message || '查询地块信息失败');
    }
  } catch (error) {
    console.error('fetchPlotListApi error:', error);
    throw error;
  }
};

/**
 * 添加地块信息
 * @param {object} plotInfo - 地块信息
 * @param {number} plotInfo.id - 地块编码
 * @param {string} plotInfo.coordinate - 地块坐标 (JSON 字符串)
 * @param {string} plotInfo.username - 农户姓名
 * @param {string} plotInfo.plotName - 地块名称
 * @returns {Promise<{ code: number, message: string }>} 接口响应数据
 */
export const addPlotApi = async (plotInfo) => {
  try {
    // 确保 coordinate 在这里作为 geometry 发送
    const payload = {
        id: plotInfo.id,
        coordinate: plotInfo.coordinate, // 保持 coordinate 字段名，后端会处理
        username: plotInfo.username,
        plotName: plotInfo.plotName
    };
    const response = await request.post('/data_management/plot', payload);
    if (response.code === 1) {
      return response;
    } else {
      throw new Error(response.message || '添加地块失败');
    }
  } catch (error) {
    console.error('addPlotApi error:', error);
    throw error;
  }
};

/**
 * 编辑地块信息
 * @param {object} plotInfo - 新的地块信息
 * @param {number} plotInfo.id - 地块编码 (必须)
 * @param {string} [plotInfo.username] - 农户姓名 (可选)
 * @param {string} [plotInfo.coordinate] - 地块坐标 (JSON 字符串, 可选)
 * @param {string} [plotInfo.cropName] - 作物名称 (可选)
 * @returns {Promise<{ code: number, message: string }>} 接口响应数据
 */
export const editPlotApi = async (plotInfo) => {
  try {
    // 确保 coordinate 在这里作为 geometry 发送
    const payload = {
        id: plotInfo.id,
        username: plotInfo.username,
        coordinate: plotInfo.coordinate,
        cropName: plotInfo.cropName
    };
    const response = await request.put('/data_management/plot', payload);
    if (response.code === 1) {
      return response;
    } else {
      throw new Error(response.message || '编辑地块失败');
    }
  } catch (error) {
    console.error('editPlotApi error:', error);
    throw error;
  }
};

/**
 * 删除地块信息
 * @param {number} id - 地块编码
 * @returns {Promise<{ code: number, message: string }>} 接口响应数据
 */
export const deletePlotApi = async (id) => {
  console.log("正在调用删除地块接口");

  try {
    const response = await request.delete('/data_management/plot', {
      params: { id: id }
    });
    if (response.code === 1) {
      return response;
    } else {
      throw new Error(response.message || '删除地块失败');
    }
  } catch (error) {
    console.error('deletePlotApi error:', error);
    throw error;
  }
};

/**
 * 模拟获取工作空间作物列表
 * 注意：在实际项目中，这通常会放在一个单独的 API 文件中 (例如 src/api/workspaceCrop.js)
 * 并通过上面的 `request` 实例进行实际的 HTTP 请求。
 * 此处是为了演示目的，直接在此文件中提供一个模拟实现。
 *
 * @param {Object} params - 查询参数
 * @param {string} [params.cropName] - 作物名称（用于模糊查询）
 * @param {number} [params.workspaceId] - 工作空间ID（用于筛选特定工作空间的作物）
 * @returns {Promise<{ code: number, message: string, list: Array, total: number }>} 接口响应数据
 */
export const fetchWorkspaceCropListApi = async (params = {}) => {
  console.log("正在调用模拟的获取工作空间作物列表接口", params);

  // 模拟网络延迟
  await new Promise(resolve => setTimeout(resolve, 500));

  const cropNameQuery = params.cropName ? String(params.cropName).toLowerCase() : '';
  const workspaceIdQuery = params.workspaceId; // 可以用来模拟按工作空间筛选

  let mockAllCrops = [
    { id: 101, name: '春小麦', type: '谷物', workspaceId: 1 },
    { id: 102, name: '冬小麦', type: '谷物', workspaceId: 1 },
    { id: 103, name: '甜玉米', type: '谷物', workspaceId: 1 },
    { id: 104, name: '糯玉米', type: '谷物', workspaceId: 2 },
    { id: 105, name: '粳稻', type: '谷物', workspaceId: 1 },
    { id: 106, name: '籼稻', type: '谷物', workspaceId: 2 },
    { id: 107, name: '大豆', type: '豆类', workspaceId: 1 },
    { id: 108, name: '绿豆', type: '豆类', workspaceId: 2 },
    { id: 109, name: '棉花', type: '经济作物', workspaceId: 1 },
    { id: 110, name: '马铃薯', type: '蔬菜', workspaceId: 1 },
    { id: 111, name: '番茄', type: '蔬菜', workspaceId: 2 },
    { id: 112, name: '草莓', type: '水果', workspaceId: 1 },
  ];

  let filteredCrops = mockAllCrops;

  if (workspaceIdQuery !== undefined && workspaceIdQuery !== null) {
    filteredCrops = filteredCrops.filter(crop => crop.workspaceId === parseInt(workspaceIdQuery));
  }

  if (cropNameQuery) {
    filteredCrops = filteredCrops.filter(crop => crop.name.toLowerCase().includes(cropNameQuery));
  }

  // 模拟成功或失败响应
  if (filteredCrops.length > 0 || cropNameQuery === '') { // 如果有匹配或查询为空时返回所有
    return {
      code: 1,
      message: '获取工作空间作物列表成功',
      list: filteredCrops,
      total: filteredCrops.length
    };
  } else {
    // 模拟没有找到匹配作物的情况
    return {
      code: 0,
      message: '没有找到匹配的作物',
      list: [],
      total: 0
    };
  }
};
