import instance from './axios';

export async function retriveChats(userId){
     try{
      const response = await instance.get('/users/' + userId + '/chats/');
      return response.data;
    } catch (error) {
    console.error("Errore:", error);
    } 
    return;
}

export async function createChat(id1,id2){
  try{
    await instance.post('/users/'+id1+'/chats/',{type: "chat",name:"",user_ids: [parseInt(id1), parseInt(id2)]});
  }catch(error){
    console.error("errore:",error);
  }
}

export async function sendMessage(chatID,text,type,isGroup){
  try{
    await instance.post('/users/' + localStorage.getItem('userId') + '/chats/' + chatID+"?isGroup="+isGroup, { text: text,contentType: type });
  }catch(error){
    console.error("errore:",error);
  }
}

export function messagesAreEqual(a, b) {
  if (a.length !== b.length) return false;

  for (let i = 0; i < a.length; i++) {
    if (
      a[i].allRead !== b[i].allRead
    ) {
      return false;
    }
  }

  return true;
}

export async function createGroup(user_ids){

}

export async function getAuthorId(userID){
  try{
    const response = await instance.get('/users/' + userID+'/username');
    return response.data.username;
  }catch(error){
    console.error("errore:",error);
    return null;
  }
}

export async function read(chatID,userID,isGroup,messageID){
  try{
    await instance.put('/users/'+userID+'/chats/'+chatID+'/messages/'+messageID+'?isGroup='+isGroup);
    console.log("Messaggio "+messageID+" marcato come letto");
  }catch(error){
    console.error("errore:",error);
  }
}

export async function getMessages(chatID,userID,isGroup) {

    const response = await instance.get(
        '/users/'+userID+'/chats/'+chatID+'/?isGroup='+isGroup
    ).then((response) => {
        return response.data;
    }).catch((error) => {
        console.error("Errore:", error);
        return null;
    });

    return response;
}
