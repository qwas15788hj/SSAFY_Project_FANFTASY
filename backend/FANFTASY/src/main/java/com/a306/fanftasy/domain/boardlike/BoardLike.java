package com.a306.fanftasy.domain.boardlike;

import com.a306.fanftasy.domain.user.entity.User;
import com.a306.fanftasy.domain.board.entity.Board;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Builder
public class BoardLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_like_id")
    private Long boardLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board boardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_like_user_id")
    private User boardLikeUserId;
}
