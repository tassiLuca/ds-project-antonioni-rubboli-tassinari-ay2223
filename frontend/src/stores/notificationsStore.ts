import { defineStore } from 'pinia'
import { type Ref, ref } from 'vue'
import { apiEndpoints } from '@/commons/globals'
import axios from 'axios'

export interface Notification {
  "id": string,
  "date": Date,
  "type": string,
  "body": string,
  "new": boolean
}

export const useNotificationsStore = defineStore('notifications', () => {

  const notifications: Ref<Notification[]> = ref([]);
  const unreadNotifications = ref(0);

  async function getAllNotifications() {
    const response = await axios.get(`${apiEndpoints.API_SERVER}/notifications/all`);
    notifications.value = response.data.data.map((n: any) => ({
      id: n._id,
      date: new Date(n.date),
      type: n.type,
      body: n.text,
      new: !n.isRead
    }));
    console.debug(notifications.value);
    unreadNotifications.value = notifications.value!.filter((n: any) => n.new).length;
  }

  async function readNotifications() {
    console.debug(notifications.value)
    const newNotifications = notifications.value?.filter((n: Notification) => n.new);
    console.debug(newNotifications)
    for (const n1 of newNotifications) {
      console.debug(`Marking notification ${n1.id} as read...`)
      await axios.put(`${apiEndpoints.API_SERVER}/notifications/${n1.id}`)
        .then(() => unreadNotifications.value--)
        .catch((error) => console.error(error));
    }
  }

  return { notifications, unreadNotifications, getAllNotifications, readNotifications };
});