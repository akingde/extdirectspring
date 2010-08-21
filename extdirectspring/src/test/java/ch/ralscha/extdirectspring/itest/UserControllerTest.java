/**
 * Copyright 2010 Ralph Schaer <ralphschaer@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.ralscha.extdirectspring.itest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;


public class UserControllerTest {
  @Test
  @SuppressWarnings("unchecked")
  public void testPost() throws ClientProtocolException, IOException {
    HttpClient client = new DefaultHttpClient();
    HttpPost post = new HttpPost("http://localhost:9998/controller/router");
    
    List<NameValuePair> formparams = new ArrayList<NameValuePair>();
    formparams.add(new BasicNameValuePair("extTID", "2"));
    formparams.add(new BasicNameValuePair("extAction", "userController"));
    formparams.add(new BasicNameValuePair("extMethod", "updateUser"));
    formparams.add(new BasicNameValuePair("extType", "rpc"));
    formparams.add(new BasicNameValuePair("extUpload", "false"));
    formparams.add(new BasicNameValuePair("name", "Joe"));
    formparams.add(new BasicNameValuePair("age", "30"));
    UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formparams, "UTF-8");

    post.setEntity(postEntity);
    
    HttpResponse response = client.execute(post);
    HttpEntity entity = response.getEntity();
    assertNotNull(entity);
    String responseString = IOUtils.toString(entity.getContent());

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> rootAsMap = mapper.readValue(responseString, Map.class);
    assertEquals(5, rootAsMap.size());
    assertEquals("updateUser", rootAsMap.get("method"));
    assertEquals("rpc", rootAsMap.get("type"));
    assertEquals("userController", rootAsMap.get("action"));
    assertEquals(2, rootAsMap.get("tid"));
    

    Map<String, Object> result = (Map<String, Object>)rootAsMap.get("result");
    assertEquals(3, result.size());
    assertEquals("Joe", result.get("name"));
    assertEquals(30, result.get("age"));
    assertEquals(true, result.get("success"));
    

  }
}