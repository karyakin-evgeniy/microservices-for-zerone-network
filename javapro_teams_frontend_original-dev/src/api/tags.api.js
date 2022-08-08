import axios from 'axios';

class TagsError extends Error {
  constructor(errorCode, message) {
    super(message);
    this.name = this.constructor.name;
    this.errorCode = errorCode;
    this.message = message;
  }
}

const TagsApi = {
  async request(requestData) {
    try {
      const { data } = await axios(requestData);
      return data;
    } catch(error) {
      const { status, data } = error.response;
      throw new TagsError(status, data.error_description);
    }
  },

  async getTags(params) {
    const requestData = {
      method: 'get',
      url: 'tags',
      params,
    }
    return this.request(requestData);
  },

  async postTags(data) {
    const requestData = {
      method: 'post',
      url: 'tags',
      data,
    }
    return this.request(requestData);
  },

  async deleteTag(tagId) {
    const requestData = {
      method: 'delete',
      url: 'tags',
      params: {
        id: tagId
      },
    }
    return this.request(requestData);
  }
};

export { TagsError, TagsApi }
