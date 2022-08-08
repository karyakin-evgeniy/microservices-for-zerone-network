import axios from 'axios';

class SupportError extends Error {
  constructor(errorCode, message) {
    super(message);
    this.name = this.constructor.name;
    this.errorCode = errorCode;
    this.message = message;
  }
}

const SupportApi = {
  async request(requestData) {
    try {
      const { data } = await axios(requestData);
      return data;
    } catch(error) {
      const { status, data } = error.response;
      throw new SupportError(status, data.error_description);
    }
  },

  async postSupport(data) {
    const requestData = {
      method: 'post',
      url: 'support',
      data,
    }
    return this.request(requestData);
  },
};

export { SupportError, SupportApi }
