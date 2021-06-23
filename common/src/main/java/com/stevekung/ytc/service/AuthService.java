/*
 * Copyright 2017-2021 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stevekung.ytc.service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import com.stevekung.ytc.core.YouTubeChat;
import com.stevekung.ytc.utils.ChatUtils;
import net.minecraft.ChatFormatting;

/**
 * Contains methods for authorizing a user and caching credentials.
 */
public class AuthService
{
    /** Define a global instance of the HTTP transport. */
    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Define a global instance of the JSON factory. */
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /**
     * This is the directory that will be used under the minecraft config directory where OAuth tokens will
     * be stored.
     */
    public static final String CREDENTIALS_DIRECTORY = ".ytc-oauth-credentials";

    /** Define a config directory. */
    public static File CONFIG_DIR;

    /** Define a user directory by their UUID. */
    public static File USER_DIR;

    /**
     * Authorizes the installed application to access user's protected data.
     *
     * @param scopes list of scopes needed to run youtube upload.
     * @param clientSecret the client secret from Google API console
     * @param credentialDatastore name of the credential datastore to cache OAuth tokens
     */
    public static Credential authorize(Collection<String> scopes, String clientSecret, String credentialDatastore) throws IOException
    {
        // Load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new StringReader(clientSecret));

        YouTubeChat.LOGGER.info("Preparing authentication directory at {}", AuthService.USER_DIR.getPath());
        // This creates the credentials datastore at mods/.ytc-oauth-credentials/${credentialDatastore}
        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(AuthService.USER_DIR);
        DataStore<StoredCredential> datastore = fileDataStoreFactory.getDataStore(credentialDatastore);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setCredentialDataStore(datastore).build();

        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static void clearCurrentCredentials() throws IOException
    {
        File file = new File(AuthService.USER_DIR, YouTubeChatService.currentLoginProfile);

        if (file.delete())
        {
            ChatUtils.printYTMessage(TextComponentUtils.formatted("Profile " + file.getName() + " have been deleted!", ChatFormatting.GREEN));
        }
        else
        {
            ChatUtils.printExceptionMessage("Couldn't delete file or file not found!");
        }
    }

    public static void clearCredentials(String fileName) throws IOException
    {
        File file = new File(AuthService.USER_DIR, fileName);

        if (file.delete())
        {
            ChatUtils.printYTMessage(TextComponentUtils.formatted("Profile " + file.getName() + " have been deleted!", ChatFormatting.GREEN));
        }
        else
        {
            ChatUtils.printExceptionMessage("Couldn't delete file or file not found!");
        }
    }
}