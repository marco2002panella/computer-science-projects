
<template>
    <div class="chatListButton" @click="$emit('click')">    
        <!-- Avatar -->
        <img
        v-if="avatar"
        :src="avatar"
        class="sidebar-avatar"
        />
        <div v-else class="sidebar-avatar-placeholder"></div>

        <!-- nome -->
        <div class="sidebar-text">
        <h4 class="got-names">{{ name }}</h4>
        </div>
        <!-- Data -->
        <span v-if="date" class="chat-date">
        {{ formatDate(date) }}
        </span>
    </div>
</template>

<script>
export default {
    name: "chatListButton",
    props: {
        name: String,
        img_URL: String,
        lastMessageDate: String,
    },
    emits: ['click'],
    methods: {
    formatDate(date) {
      if (!date) return "";
      return new Date(date).toLocaleDateString("it-IT", {
        day: "2-digit",
        month: "2-digit"
      });
    }
  }

}
</script>

<style scoped>
.sidebar-chat-item {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 10px;
  cursor: pointer;

  /* border box stile card */
  border: 1px solid rgba(0, 0, 0, 0.08);
  background-color: #fff;

  transition: all 0.2s ease;
}

.sidebar-chat-item.on-mouse-create::after {
  content: "CREA";
  position: absolute;
  top: 20px; 
  left: 85%;
  transform: translateX(-50%);
  
  background-color: white;
  border: 1px solid #ccc;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 10px;
  color: #333;
  white-space: nowrap;

  opacity: 0;
  pointer-events: none;
  transition: opacity 0.2s;
  z-index: 100;
}

.sidebar-chat-item.on-mouse-add::after {
  content: "AGGIUNGI";
  position: absolute;
  top: 20px; 
  left: 85%;
  transform: translateX(-50%);
  
  background-color: white;
  border: 1px solid #ccc;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 10px;
  color: #333;
  white-space: nowrap;

  opacity: 0;
  pointer-events: none;
  transition: opacity 0.2s;
  z-index: 100;
}

.sidebar-chat-item.on-mouse-add:hover::after {
  opacity: 1;
}
.sidebar-chat-item.on-mouse-create:hover::after {
  opacity: 1;
}


.sidebar-chat-item:hover {
  background-color: #f5f5f5;
  transform: translateY(-1px);
}

.sidebar-avatar,
.sidebar-avatar-placeholder {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  object-fit: cover;

  display: flex;
  align-items: center;
  justify-content: center;

  background-color: #d9d9d9;
  margin-right: 10px;
}

.sidebar-text {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
}

.got-names {
  font-size: 15px;
  font-weight: 600;
  color: #222; /* nero ma non aggressivo */
  margin: 0;
  line-height: 1.2;
}

.chat-date {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  margin-left: 10px;
}
</style>