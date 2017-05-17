package com.codeu.chat.client.angular;


import java.util.Scanner;

import codeu.chat.client.ClientContext;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.common.ConversationSummary;
import codeu.chat.util.Logger;

import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.spec.PBEKeySpec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Root resource (exposed at "wechat" path)
 * 
 * Based off of resources given at: 
 * https://jersey.java.net/documentation/latest/getting-started.html#new-from-archetype
 *
 */
@Path("wechat")
public final class WeChat {
    private final static Logger.Log LOG = Logger.newLog(WeChat.class);

    private static final String PROMPT = ">>";

    private final static int PAGE_SIZE = 10;

    private boolean alive = true;

    private final ClientContext clientContext;

    private final SecureRandom random = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    public static int globalInt = 0;

    // Argument-less constructor
    public WeChat(){
        this.clientContext = null;
        globalInt = globalInt + 1;
        System.out.println(globalInt);
        System.out.println("no arg constructor");
    }

    public WeChat(String name){
        this.clientContext = null;
        System.out.println("1 arg constructor");
    }

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("testtext")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "This text is being sent from java backend!";
    }

}


