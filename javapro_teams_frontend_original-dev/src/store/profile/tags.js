import { TagsError, TagsApi } from '@/api/tags.api'

const concatTags = ({ tags, data, offset}) => {
  if (!offset) {
    return data;
  }

  const allTags = [...tags, ...data];
  const unicTags = new Set(allTags);
  return [...unicTags];
};

const saveTagsData = ({ state, commit, response }) => {
  const tags = state.tags;
  const { total, offset, perPage, data } = response;
  const allTags = concatTags({ tags, data, offset});
  commit('updateTags', allTags);
  commit('updatePageParams', { total, offset, perPage });
};

const saveTagsError = (commit, error) => {
  if (error instanceof TagsError) {
    commit('updateTagsError', {
      errorCode: error.errorCode,
      errorMessage: error.message,
    })
  }
  return false;
};

const state = () => ({
  tags: null,
  pageParams: '',
  tagsError: null,
});

const getters = {
  tags: (state) => state.tags,
  pageParams: (state) => state.pageParams,
  tagsError: (state) => state.tagsError,
};

const mutations = {
  updateTags(state, newData) {
    state.tags = newData;
  },
  updatePageParams(state, newParams) {
    state.pageParams = newParams;
  },
  updateTagsError(state, errorData) {
    state.tagsError = errorData;
  }
};

const actions = {
  async getTags(context, params = {}) {
    const { state, commit } = context;

    try {
      const response = await TagsApi.getTags(params);
      saveTagsData({ state, commit, response });
      return true;
    } catch(error) {
      saveTagsError(commit, error);
    }
  },
  async createTag(context, tag) {
    const { commit, dispatch } = context;
    try {
      await TagsApi.postTags({ id: 0, tag })
        .then(() => dispatch('getTags'));
      return true;
    } catch(error) {
      saveTagsError(commit, error);
    }
  },
  async removeTag(context, tagId) {
    const { commit, dispatch } = context;
    try {
      await TagsApi.deleteTag(tagId)
        .then(() => dispatch('getTags'));
      return true;
    } catch(error) {
      saveTagsError(commit, error);
    }
  }
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
}
