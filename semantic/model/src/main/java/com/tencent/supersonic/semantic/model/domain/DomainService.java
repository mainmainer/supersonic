package com.tencent.supersonic.semantic.model.domain;

import com.tencent.supersonic.auth.api.authentication.pojo.User;
import com.tencent.supersonic.semantic.api.model.request.DomainReq;
import com.tencent.supersonic.semantic.api.model.request.DomainSchemaFilterReq;
import com.tencent.supersonic.semantic.api.model.request.DomainUpdateReq;
import com.tencent.supersonic.semantic.api.model.response.DomainResp;
import com.tencent.supersonic.semantic.api.model.response.DomainSchemaResp;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DomainService {


    DomainResp getDomain(Long id);

    String getDomainFullPath(Long domainId);

    Map<Long, String> getDomainFullPath();

    void createDomain(DomainReq domainReq, User user);

    void updateDomain(DomainUpdateReq domainUpdateReq, User user);

    void deleteDomain(Long id);

    String getDomainBizName(Long domainId);

    List<DomainResp> getDomainList();

    List<DomainResp> getDomainList(List<Long> domainIds);

    Map<Long, DomainResp> getDomainMap();

    List<DomainResp> getDomainListForAdmin(String userName);

    List<DomainResp> getDomainListForViewer(String userName);

    Set<DomainResp> getDomainChildren(List<Long> domainId);

    List<DomainSchemaResp> fetchDomainSchema(DomainSchemaFilterReq filter, User user);

}