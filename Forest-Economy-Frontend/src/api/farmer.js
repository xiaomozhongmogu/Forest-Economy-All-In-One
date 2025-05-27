import request from "@/utils/request";

/**
 * 查询农户信息
 * @param {object} params - 查询参数
 * @param {number} params.currentPage - 当前页数 (对应接口的 page)
 * @param {number} params.pageSize - 每页条目数 (对应接口的 pageSize)
 * @param {string} [params.name] - 农户的姓名 (对应接口的 username, 可选)
 * @param {string} [params.phone] - 联系电话 (对应接口的 phoneNumber, 可选)
 * @returns {Promise<object>} 接口响应数据 { list: [], total: number }
 */





export const fetchFamersApi = async (params) => {
  console.log('fetchFamersApi 被调用了，参数是:', params); 
  try {
    const response = await request({
      url: '/data_management/user',
      method: 'GET',
      params: {
        page: params.currentPage,
        pageSize: params.pageSize,
        username: params.name,
        phoneNumber: params.phone
      }
    });

    if (response.code === 1) {
      return {
        list: response.data.rows,
        total: response.data.total
      };
    } else {
      throw new Error(response.message || '查询农户信息失败');
    }
  } catch (error) {
    console.error('API Error: fetchFamersApi', error);
    throw error;
  }
};

/**
 * 编辑农户信息
 * @param {object} params - 编辑参数
 * @param {number} params.id - 农户ID (对应接口的 id)
 * @param {string} params.name - 农户姓名 (对应接口的 username)
 * @param {string} params.phone - 联系电话 (对应接口的 phoneNumber)
 *
 */
export const editFamerApi = async (data) => {
  console.log('editFamerApi 被调用了，发送的数据是:', data);
  try {
    const response = await request({
      url: '/data_management/user',
      method: 'PUT',
      data: {
        id: data.id,
        username: data.username,
        phoneNumber: data.phoneNumber,
        residenceAddress: data.residenceAddress
      }
    });

    if (response.code === 1) {
      return response;
    } else {
      throw new Error(response.message || '编辑农户信息失败');
    }
  } catch (error) {
    console.error('API Error: editFamerApi', error);
    throw error;
  }
};





