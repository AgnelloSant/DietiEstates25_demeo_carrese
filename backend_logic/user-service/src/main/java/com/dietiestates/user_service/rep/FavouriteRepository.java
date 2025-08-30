package com.dietiestates.user_service.rep;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import com.dietiestates.user_service.model.Favourite;
import com.dietiestates.user_service.model.FavouriteId;



public interface FavouriteRepository extends JpaRepository<Favourite, FavouriteId> {

     interface PropertyRow {
    Long getId();
    String getTitle();
    String getCity();
    Double getArea();
    Double getPrice();
  }

     @Query(value = """
    select 
      p.id       as id,
      p.title    as title,
      p.city     as city,
      p.area_mq  as area,   -- alias! (colonna = area_mq → alias = area)
      p.price    as price
    from properties p
    join favourites f on f.id_prop = p.id
    where f.id_user = :userId
    order by f.created_at desc
  """, nativeQuery = true)
  List<PropertyRow> findFavouriteRowsByUser(@Param("userId") Long userId);

    long countByIdUserId(Long userId);

    boolean existsByIdUserIdAndIdPropId(Long userId, Long propId);

    @Modifying
    @Query("delete from Favourite f where f.id.userId = :userId and f.id.propId = :propId")
    int deleteByUserAndProp(@Param("userId") Long userId, @Param("propId") Long propId);
}

