package swp.group2.swpbe.user;

import org.springframework.stereotype.Repository;

import swp.group2.swpbe.user.entities.Wallet;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Wallet findByUserId(int id);
}