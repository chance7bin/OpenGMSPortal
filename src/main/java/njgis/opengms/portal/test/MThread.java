package njgis.opengms.portal.test;

import njgis.opengms.portal.dao.NoticeDao;
import njgis.opengms.portal.entity.po.Notice;

/**
 * @Description
 * @Author bin
 * @Date 2022/06/30
 */
public class MThread implements Runnable{

    // @Autowired
    // NoticeDao noticeDao;
    private NoticeDao noticeDao;

    public MThread(NoticeDao noticeDao) {
        this.noticeDao = noticeDao;
    }

    public MThread() {
    }

    private int ticket = 100;

    @Override
    public void run() {

        while (true){
            // System.out.println(Thread.currentThread().getName() + ":" + i);

            addOne("2ee4d0dd-9099-458d-a95c-6b49ce4761a3");
            // addOneSynchronized("2ee4d0dd-9099-458d-a95c-6b49ce4761a3");


            // synchronized (this){
            //     if (ticket > 0) {
            //         try {
            //             Thread.sleep(100);
            //         } catch (InterruptedException e) {
            //             e.printStackTrace();
            //         }
            //
            //         System.out.println(Thread.currentThread().getName() + ":卖票，票号为：" + ticket);
            //         ticket--;
            //     } else {
            //         break;
            //     }
            // }



        }

    }

    void addOne(String noticeId){

        Notice notice = noticeDao.findFirstById(noticeId);
        //Exception in thread "线程1" Exception in thread "线程2" Exception in thread "线程3" java.lang.NullPointerException
        //在使用Thread的时候Autowired失效，需通过构造器传入依赖

        boolean hasRead = notice.isHasRead();
        //true就说明有人在操作
        if (hasRead){
            System.out.println(Thread.currentThread().getName() + ":这个notice锁住了，这次+1失败");
            return;
        } else {
            //没有被锁住的话赶快做个标记
            notice.setHasRead(true);
            noticeDao.save(notice);
        }
        String message = notice.getMessage();
        System.out.println(Thread.currentThread().getName() + ":message:" + message);
        int i = Integer.parseInt(message);
        i += 1;
        notice.setMessage(String.valueOf(i));
        notice.setHasRead(false);
        noticeDao.save(notice);

    }

    void addOneSynchronized(String noticeId){

        while(true){
            synchronized (this){

                Notice notice = noticeDao.findFirstById(noticeId);
                boolean hasRead = notice.isHasRead();
                //true就说明有人在操作
                if (hasRead){
                    System.out.println(Thread.currentThread().getName() + ":这个notice锁住了，这次+1失败");
                    continue;
                } else {
                    //没有被锁住的话赶快做个标记
                    notice.setHasRead(true);
                    noticeDao.save(notice);
                }
                String message = notice.getMessage();
                System.out.println(Thread.currentThread().getName() + ":message:" + message);
                int i = Integer.parseInt(message);
                i += 1;
                notice.setMessage(String.valueOf(i));
                notice.setHasRead(false);
                noticeDao.save(notice);
                break;

            }

        }

    }


}
