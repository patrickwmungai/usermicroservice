package com.finessence.user.controller;

import com.finessence.user.entities.*;
import com.finessence.user.model.ApiResponse;
import com.finessence.user.model.PermssionHolder;
import com.finessence.user.model.ResponseCodes;
import com.finessence.user.model.Token;
import com.finessence.user.repository.CrudService;
import com.finessence.user.services.GlobalFunctions;
import java.util.List;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 *
 * @author patrick
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    private final Logger LOG = Logger.getLogger(RoleController.class);

    @Autowired
    Environment env;

    @Autowired
    CrudService crudService;

    @Autowired
    GlobalFunctions globalFunctions;

    @Autowired
    ResponseCodes responseCodes;
    private Calendar currenttime = Calendar.getInstance();

    /**
     * Function to Create new Roles
     *
     * @param authKey
     * @param role
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> create(@RequestHeader(value = "Authorization") String authKey, @RequestBody Role role) {
        ResponseEntity<?> res = null;
        try {
            Gson gson = new Gson();
            Token token = globalFunctions.parseJWT(authKey);
            role.setCreatedBy(Integer.parseInt(token.getUserid()));
            if (role.getGroupId() == null || role.getGroupId().equals("")) {
                role.setGroupId(token.getGroupID());
            }
            role.setId(0);
            role.setTimeCreated(new Date());
            role.setTimeUpdated(new Date());
            role.setIntrash("NO");
            Object entityId = crudService.save(role);

            Role entity = globalFunctions.findRoleByNameAndGroupIdAndIntrash(role.getName(), role.getGroupId(), "NO");

            if (entity != null) {
                for (Permission permission : role.getPermissions()) {
                    PermissionMap permissionMap = new PermissionMap();
                    permissionMap.setId(0);
                    permissionMap.setRoleId(entity.getId());
                    permissionMap.setCreatedBy(role.getCreatedBy());
                    permissionMap.setApprovalStatus(0);
                    permissionMap.setCurrentApprovalLevel(0);
                    permissionMap.setIntrash("NO");
                    permissionMap.setMaxApprovals(0);
                    permissionMap.setStatus(1);
                    permissionMap.setTimeCreated(new Date(currenttime.getTime().getTime()));
                    permissionMap.setPermissionId(permission.getId());

                    LOG.info("Request:" + gson.toJson(role));
                    crudService.save(permissionMap);
                }
                LOG.info(entity.getName());
                ApiResponse SUCCESS = responseCodes.SUCCESS;
                SUCCESS.setEntity(entity);
                res = new ResponseEntity<>(SUCCESS, HttpStatus.OK);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error("Error Occured:" + ex.getMessage());
            res = new ResponseEntity<>(responseCodes.EXCEPTION_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return res;
    }

    /**
     *
     * Function to be called during user Creation To load permissions
     */
    @RequestMapping(value = "/preCreate", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> preCreate(@RequestHeader(value = "Authorization") String authKey) {
        ResponseEntity<?> res = null;

        try {

            //all permissions for logged in user
            Token token = globalFunctions.parseJWT(authKey);

            List<Permission> entity = globalFunctions.findPermissionByUserId(Integer.parseInt(token.getUserid()));
            List<PermssionHolder> permssionHolder = formatPermssions(entity);

            ApiResponse SUCCESS = responseCodes.SUCCESS;
            SUCCESS.setEntity(permssionHolder);
            res = new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error("Error Occured:" + ex.getMessage());
            res = new ResponseEntity<>(responseCodes.EXCEPTION_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return res;
    }

    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> findById(@RequestHeader(value = "Authorization") String authKey, @RequestParam("id") String id) {
        ResponseEntity<?> res = null;

        try {
            /*
             description
            Get selected role
            Get selected role permissions
            Get all looged in user roles permissions
            loop though looged in user permissions
            for each looged in permissions loop the selected role permissions
            compare if the given permissions is equal and flag yes if so
             */
            Role entity = crudService.findEntity(Integer.parseInt(id), Role.class);
            //fetch permissions of a given role
            List<Permission> selectedRolePermissions = globalFunctions.findPermissionByRoleId(entity.getId());

            //all permissions for logged in user
            Token token = globalFunctions.parseJWT(authKey);

            List<Permission> loggedInUserPermissions = globalFunctions.findPermissionByUserId(Integer.parseInt(token.getUserid()));

            for (Permission loggedInserPermission : loggedInUserPermissions) {
                for (Permission selectedRolePermission : selectedRolePermissions) {
                    if (loggedInserPermission.getId().equals(selectedRolePermission.getId())) {
                        loggedInserPermission.setHas_permission(true);
                    }
                }
            }
            if (loggedInUserPermissions.size() > 0) {
                entity.setPermissions(loggedInUserPermissions);
                List<PermssionHolder> permssionHolder = formatPermssions(loggedInUserPermissions);
                entity.setPermissionsMap(permssionHolder);
            }

            LOG.info(entity.getName());

            ApiResponse SUCCESS = responseCodes.SUCCESS;
            SUCCESS.setEntity(entity);
            res = new ResponseEntity<>(SUCCESS, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error("Error Occured:" + ex.getMessage());
            res = new ResponseEntity<>(responseCodes.EXCEPTION_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return res;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Transactional
    public ResponseEntity<?> update(@RequestHeader(value = "Authorization") String authKey, @RequestBody Role role) {
        ResponseEntity<?> res = null;
        try {
            Token token = globalFunctions.parseJWT(authKey);

            role.setCreatedBy(Integer.parseInt(token.getUserid()));

            Gson gson = new Gson();
            LOG.info("Request:" + gson.toJson(role));

            crudService.saveOrUpdate(role);

            globalFunctions.deleteByRoleId(role.getId());

            for (Permission permission : role.getPermissions()) {
                PermissionMap permissionMap = new PermissionMap();
                permissionMap.setId(0);//to be revisited
                permissionMap.setRoleId(role.getId());
                permissionMap.setCreatedBy(role.getCreatedBy());
                permissionMap.setApprovalStatus(0);
                permissionMap.setCurrentApprovalLevel(0);
                permissionMap.setIntrash("NO");
                permissionMap.setMaxApprovals(0);
                permissionMap.setStatus(1);
                permissionMap.setTimeCreated(new Date(currenttime.getTime().getTime()));
                permissionMap.setPermissionId(permission.getId());
                LOG.info("Request:" + gson.toJson(permissionMap));
                crudService.save(permissionMap);
            }

            ApiResponse SUCCESS = responseCodes.SUCCESS;
            SUCCESS.setEntity(role);
            res = new ResponseEntity<>(SUCCESS, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error("Error Occured:" + ex.getMessage());
            res = new ResponseEntity<>(responseCodes.EXCEPTION_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return res;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> delete(@RequestHeader(value = "Authorization") String authKey, @RequestBody Role role) {
        ResponseEntity<?> res = null;

        return res;
    }

    @RequestMapping(value = "/read", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> read(@RequestHeader(value = "Authorization") String authKey, @RequestParam("start") int start, @RequestParam("end") int end) {
        ResponseEntity<?> res = null;

        try {
            Token token = globalFunctions.parseJWT(authKey);
            String addedQueryfilter = "";
            Map<String, Object> params = new HashMap<>();
            LOG.info("Fetching data for Group:" + token.getGroupID());

            List<Role> entity = null;
            if (token.getIsadminagent().equals("NO")) {
                addedQueryfilter = " WHERE r.groupId =:id ";
                params = Collections.singletonMap("id", token.getGroupID());
            }
            String q = "select r from Role r  " + addedQueryfilter;
            if (token.getIsadminagent().equals("NO")) {
                entity = crudService.fetchWithHibernateQuery(q, params, start, end);
            } else {
                entity = crudService.fetchWithHibernateQuery(q, Collections.EMPTY_MAP, start, end);

            }

            LOG.info("Fetched List of:" + entity.size());

            ApiResponse SUCCESS = responseCodes.SUCCESS;
            SUCCESS.setEntity(entity);
            res = new ResponseEntity<>(SUCCESS, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error("Error Occured:" + ex.getMessage());
            res = new ResponseEntity<>(responseCodes.EXCEPTION_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return res;
    }

    /**
     * Get Permissions loop through and get permissions Module create a new
     * permission holder add permissions to the holder For each check if module
     * has changed to create new holder Add all permissions on a given module to
     * the holder
     *
     * @param permissions
     * @return
     */
    public List<PermssionHolder> formatPermssions(List<Permission> permissions) {

        List<PermssionHolder> permssionHolder = new ArrayList<>();
        String module = null;
        String icon = null;
        List<Permission> holderPermissions = null;
        PermssionHolder holder = null;
        int p = 1;
        for (Permission permission : permissions) {

            if (module == null || module.isEmpty()) {//Only executed at first loop to initiate values                    
                module = permission.getModule();
                icon = permission.getModuleIcon();
                holder = new PermssionHolder();
                holderPermissions = new ArrayList<>();
            }

            if (module.equals(permission.getModule())) {//as long as module is same
                holderPermissions.add(permission);

                if (permissions.size() == p) {//if is last record
                    //add all permissions and create the holder
                    if (holderPermissions.size() > 0) {
                        holder.setPermissions(holderPermissions);
                        holder.setModule(module);
                        holder.setIcon(icon);
                        //add holder to permissions
                        permssionHolder.add(holder);
                    }
                }

            } else {//when moudule changes
                //add all permissions and create the holder
                if (holderPermissions.size() > 0) {
                    holder.setPermissions(holderPermissions);
                    holder.setModule(module);
                    holder.setIcon(icon);
                    //add holder to permissions Holder
                    permssionHolder.add(holder);
                }

                module = permission.getModule();
                icon = permission.getModuleIcon();
                holder = new PermssionHolder();
                holderPermissions = new ArrayList<>();
                holderPermissions.add(permission);
            }
            p++;

        }

        return permssionHolder;
    }

}
