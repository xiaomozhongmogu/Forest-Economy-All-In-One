<template>
  <div class="community-content">
    <h1>社区</h1>
    <div class="post-list">
      <div class="post-item" v-for="post in posts" :key="post.id">
        <h2>{{ post.title }}</h2>
        <p>{{ post.content }}</p>
        <div class="post-meta" v-if="post.author">
          <span class="post-author">{{ post.author }}</span>
          <span class="post-time">{{ post.time }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CommunityContent',
  data() {
    return {
      posts: [
        {
          id: 1,
          title: '社区公告',
          content: '欢迎来到社区！',
          author: '社区管理员',
          time: '2024-03-20'
        },
        {
          id: 2,
          title: '活动通知',
          content: '本周末有一个林下经济产业发展规划的讲座，欢迎参加。',
          author: '村委会',
          time: '2024-03-19'
        },
        {
          id: 3,
          title: '关于《林下经济产业发展规划》的通知',
          content: '为促进本村林下经济发展，村委会特制定《林下经济产业发展规划》，请各位村民认真阅读并积极参与。规划内容包括：1. 林下食用菌种植基地建设；2. 林下中药材种植项目；3. 林下养殖产业发展计划。具体内容请到村委会办公室查阅。',
          author: '村委会',
          time: '2024-03-21'
        }
      ]
    }
  }
}
</script>

<style scoped>
.community-content {
  padding: 20px;
  background-color: #FDFBF7;
  color: #2B3F2B;
}

.post-list {
  margin-top: 20px;
}

.post-item {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #ffffff;
  border: 1px solid #E8E0D0;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.post-item h2 {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #556B2F;
}

.post-item p {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #3C2A1A;
  line-height: 1.6;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #7D7060;
}

.post-author {
  font-weight: 500;
}

.post-time {
  color: #9E9E9E;
}
</style>
