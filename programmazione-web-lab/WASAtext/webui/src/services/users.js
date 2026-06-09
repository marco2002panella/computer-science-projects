import instance from './axios';

export async function searchByName(searchQuery="") { 
  if (searchQuery.trim() === '') {
      return;
    }
  try {
    const response = await instance.post("users/", {prefix: searchQuery});
    return response.data;
  } catch (error) {
    console.error('Errore durante la ricerca degli utenti:', error);
    return [];
  }
}